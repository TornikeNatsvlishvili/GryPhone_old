package com.Tornike.Gryphone.Notes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import com.Tornike.Gryphone.R;
import com.google.android.maps.OverlayItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class NoteActivity extends Activity {
	// Adapters
	NotePagerAdapter pagerAdapter;

	// Views
	ViewPager viewPager;
	ImageButton addNoteBtn, deleteNoteBtn, noteListBtn;
	TextView pageCounterTxt;
	TextView topText;
	ImageView topButton;

	// Variables
	ArrayList<String> noteTitles = new ArrayList<String>();
	ArrayList<LinearLayout> layoutList = new ArrayList<LinearLayout>();
	ArrayList<EditText> editTextList = new ArrayList<EditText>();
	ArrayList<TextView> titleTextList = new ArrayList<TextView>();
	String currentText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notes_layout);

		// adapter init
		pagerAdapter = new NotePagerAdapter(this);

		// VIew init
		viewPager = (ViewPager) findViewById(R.id.note_pager);
		addNoteBtn = (ImageButton) findViewById(R.id.notes_create_btn);
		deleteNoteBtn = (ImageButton) findViewById(R.id.notes_delete_btn);
		pageCounterTxt = (TextView) findViewById(R.id.note_page_counter);
		topText = (TextView) findViewById(R.id.top_bar_text);
		topButton = (ImageView) findViewById(R.id.top_bar_icon);
		noteListBtn = (ImageButton) findViewById(R.id.top_bar_notes_btn);

		viewPager.setAdapter(pagerAdapter);
		topText.setText("Notes");

		// top bar button to go back to main
		topButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		noteListBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CharSequence[] noteTitleList = new CharSequence[noteTitles
						.size()];
				int length = noteTitles.size();
				for (int a = 0; a < length; a++) {
					noteTitleList[a] = noteTitles.get(a);
				}
				AlertDialog.Builder noteListDialog = new AlertDialog.Builder(
						NoteActivity.this);
				noteListDialog.setTitle("Pick a note:");
				noteListDialog.setItems(noteTitleList,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								viewPager.setCurrentItem(item, true);
							}
						});
				noteListDialog.show();
			}
		});
		addNoteBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final EditText input = new EditText(NoteActivity.this);
				AlertDialog.Builder inputDialog = new AlertDialog.Builder(
						NoteActivity.this);
				inputDialog.setTitle("Enter the note's title:");
				inputDialog.setView(input);
				inputDialog.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								String title = input.getText().toString();
								if (title.equals("")) {
									Toast.makeText(NoteActivity.this,
											"Not a proper title!",
											Toast.LENGTH_LONG).show();
									return;
								}
								for (int a = 0; a < noteTitles.size(); a++) {
									if (title.equals(noteTitles.get(a))) {
										Toast.makeText(
												NoteActivity.this,
												"A note with this title already exists!",
												Toast.LENGTH_LONG).show();
										return;
									}
								}
								pagerAdapter.addItem(getPagerItem(title, ""));
								viewPager.setCurrentItem(
										pagerAdapter.getCount() - 1, true);
								noteTitles.add(title);
							}
						});
				inputDialog.show();
			}
		});
		deleteNoteBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// if there is one page left dont delete
				if (pagerAdapter.getCount() == 1) {
					return;
				}
				// ask if the user is sure they want to delete
				AlertDialog.Builder areYouSure = new AlertDialog.Builder(
						NoteActivity.this);
				areYouSure.setTitle("Confirm");
				areYouSure
						.setMessage("Are you sure you want to delete this note?");
				areYouSure.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// Delete all saved stuff
								int length = layoutList.size();
								for (int a = 0; a < length; a++) {
									fileExistsAndDelete(a);
								}
								// Remove specific index
								layoutList.remove(viewPager.getCurrentItem());
								noteTitles.remove(viewPager.getCurrentItem());
								editTextList.remove(viewPager.getCurrentItem());
								titleTextList.remove(viewPager.getCurrentItem());
								pagerAdapter.removeItem(viewPager
										.getCurrentItem());

								// Resave the desired items
								try {
									saveData();
								} catch (Exception e) {
									Log.e("ERROR", "save data", e);
								}

								// Reset the page counter
								pageCounterTxt.setText((viewPager
										.getCurrentItem() + 1)
										+ "/"
										+ pagerAdapter.getCount());
							}
						});
				areYouSure.setNegativeButton("Cancel", null);
				areYouSure.show();
			}
		});
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int arg0) {

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int arg0) {
				pageCounterTxt.setText((viewPager.getCurrentItem() + 1) + "/"
						+ pagerAdapter.getCount());
			}
		});
		// Read saved data
		try {
			String[][] result = readData();
			int length = result[0].length;
			for (int a = 0; a < length; a++) {
				pagerAdapter.addItem(getPagerItem(result[0][a], result[1][a]));
			}
			if (length != 0) {
				pageCounterTxt.setText("1/" + length);
			} else {
				pageCounterTxt.setText("1/1");
			}

		} catch (Exception e) {
			Log.e("ERROR", "read data", e);
		}
		// Check if no notes exists if yes add one
		if (pagerAdapter.getCount() == 0) {
			pagerAdapter.addItem(getPagerItem("New Note", ""));
			noteTitles.add("New Note");
		}
	}

	public LinearLayout getPagerItem(String title, String note) {
		LinearLayout layout = (LinearLayout) ((LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.note_pager_layout, null);
		final TextView tt = (TextView) layout
				.findViewById(R.id.note_title_text);
		EditText et = (EditText) layout.findViewById(R.id.notes_edit_text);

		tt.setText(title);
		tt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final EditText input = new EditText(NoteActivity.this);

				AlertDialog.Builder inputDialog = new AlertDialog.Builder(
						NoteActivity.this);
				inputDialog.setTitle("Enter the new title:");
				inputDialog.setView(input);
				inputDialog.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								String title = input.getText().toString();
								for (int a = 0; a < noteTitles.size(); a++) {
									if (title.equals(noteTitles.get(a))) {
										Toast.makeText(
												NoteActivity.this,
												"A note with this title already exists!",
												Toast.LENGTH_LONG).show();
										return;
									}
								}
								if (title.equals("")) {
									Toast.makeText(NoteActivity.this,
											"Not a proper title!",
											Toast.LENGTH_LONG).show();
									return;
								}
								tt.setText(title);
								noteTitles.set(viewPager.getCurrentItem(),
										title);
							}
						});
				inputDialog.show();
			}
		});

		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/century_gothic.otf");
		et.setTypeface(tf);
		et.setText(note);

		layoutList.add(layout);
		editTextList.add(et);
		titleTextList.add(tt);
		return layout;
	}

	public void saveData() throws Exception {
		int length = layoutList.size();
		for (int a = 0; a < length; a++) {
			View v = layoutList.get(a);
			TextView titleTxt = (TextView) v.findViewById(R.id.note_title_text);
			EditText editTxt = (EditText) v.findViewById(R.id.notes_edit_text);
			String title = titleTxt.getText().toString();
			String note = editTxt.getText().toString();
			String FILENAME = a + "Note:" + title;
			String string = note;

			fileExistsAndDelete(a);
			FileOutputStream fos = openFileOutput(FILENAME,
					Context.MODE_PRIVATE);
			fos.write(string.getBytes());
			fos.close();
		}
	}

	public void fileExistsAndDelete(int index) {
		String[] fileList = fileList();
		int length = fileList.length;
		for (int a = 0; a < length; a++) {
			if ((fileList[a].charAt(0) - 48) == index) {
				deleteFile(fileList[a]);
				break;
			}
		}
	}

	public String[][] readData() throws Exception {
		String[] fileList = fileList();
		ArrayList<String> pureFileList = new ArrayList<String>();
		int length = fileList.length;
		for (int a = 0; a < length; a++) {
			if (fileList[a].contains("Note:")) {
				pureFileList.add(fileList[a]);
				// prepare title list size
				noteTitles.add("");
			}
		}
		String[] fileNameList = new String[pureFileList.size()];
		String[] fileNoteList = new String[pureFileList.size()];
		int counter = -1;
		for (String FILENAME : pureFileList) {
			if (FILENAME.contains("Note:")) {
				counter++;
				byte[] buffer = new byte[1024];
				StringBuffer fileContent = new StringBuffer("");

				FileInputStream fis = openFileInput(FILENAME);
				while (fis.read(buffer) != -1) {
					fileContent.append(new String(buffer));
				}
				int index = FILENAME.charAt(0) - 48;
				fileNameList[index] = FILENAME.substring(
						FILENAME.indexOf("Note:") + "Note:".length(),
						FILENAME.length()).trim();
				fileNoteList[index] = fileContent.toString().trim();
				noteTitles.set(index, fileNameList[index]);
				fis.close();
			}
		}
		return new String[][] { fileNameList, fileNoteList };
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			saveData();
		} catch (Exception e) {
			Log.e("ERROR", "write data", e);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		try {
			saveData();
		} catch (Exception e) {
			Log.e("ERROR", "write data", e);
		}
	}
}

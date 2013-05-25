/*
 * Copyright or © or Copr. Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN (03/31/2013)
 * 
 * This software is a computer program whose purpose is to guide the user during hikes.
 *
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */

package net.line2soft.preambul.views;

import net.line2soft.preambul.models.Location;
import net.line2soft.preambul.utils.RWFile;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Looper;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.Toast;
import net.line2soft.preambul.R;

/**
 * This class contains common methods for locations activities.
 * @author Équipe A (Projet Rand'OSM) - Stéphane COATHALEM, Mathieu LUCAS, Adrien PAVIE, Alexis SCOLAN
 */
public abstract class LocationActivity extends Activity {
// ATTRIBUTES
	/** The wait dialog **/
	private ProgressDialog waitDialog;

// MODIFIERS
	/**
	 * Set the location list to display
	 * @param newList The {@link Location} list
	 */
	public abstract void setLocationList(Location[] newList);

	
// OTHER METHODS
	
	@Override
	protected void onResume() {
		super.onResume();
		((ImageView)findViewById(R.id.imageView1)).setImageBitmap(RWFile.readImageBackground(R.drawable.back, this));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
	}

	@Override
	protected void onDestroy() {
		((BitmapDrawable)((ImageView)findViewById(R.id.imageView1)).getDrawable()).getBitmap().recycle();
	    super.onDestroy();
	}
	/**
	 * Displays a message to user
	 * @param message The message to display
	 */
	public void displayInfo(CharSequence message) {
		try {
			Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		}
		catch(RuntimeException e) {
			Looper.prepare();
			Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Displays a Dialog to set Internet connection
	 */
	public void showNoConnectionDialog() {
        final Context ctx = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setCancelable(true);
        builder.setMessage(R.string.message_no_connection);
        builder.setTitle(R.string.dialog_title_no_connection);
        builder.setPositiveButton(R.string.settings_button_text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ctx.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            }
        });
        builder.setNegativeButton(R.string.name_button_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	dialog.dismiss();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
            	dialog.dismiss();
            }
        });

        builder.show();
    }
	
	/**
	 * Displays a wait dialog to user
	 * @param title The title of the dialog
	 * @param message The content of the dialog (an explanation of why the user is waiting)
	 */
	public void showWaitDialog(String title, String message) {
		waitDialog=ProgressDialog.show(this, title, message);	
    }
	
	/**
	 * Hides the wait dialog
	 */
	public void hideWaitDialog(){
		waitDialog.dismiss();
	}
}
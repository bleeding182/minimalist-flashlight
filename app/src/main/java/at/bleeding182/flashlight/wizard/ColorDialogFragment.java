package at.bleeding182.flashlight.wizard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import at.bleeding182.flashlight.R;

public class ColorDialogFragment extends DialogFragment
        implements SeekBar.OnSeekBarChangeListener {

    private static final int RED = 0;
    private static final int GREEN = 1;
    private static final int BLUE = 2;
    private static final String ARG_COLOR = "color";
    private static final String ARG_NAME = "name";
    private static final String ARG_POSITION = "position";

    private SeekBar[] sliders = new SeekBar[3];
    private ColorDrawable previewDrawable;
    private View preview;
    private TextView hexvalue;

    public ColorDialogFragment() {
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    public static ColorDialogFragment newInstance(int position, String name, int color) {
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putInt(ARG_COLOR, color);
        args.putString(ARG_NAME, name);
        ColorDialogFragment fragment = new ColorDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_color, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preview = view.findViewById(R.id.preview);
        hexvalue = (TextView) view.findViewById(R.id.hexvalue);
        previewDrawable = new ColorDrawable();
        int color = getArguments().getInt(ARG_COLOR);
        previewDrawable.setColor(color);
        preview.setBackgroundDrawable(previewDrawable);

        view.findViewById(R.id.action_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        view.findViewById(R.id.action_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ColorCallback) getActivity()).onColorSelected(getArguments().getInt(ARG_POSITION), getArguments().getString(ARG_NAME), previewDrawable.getColor());
                dismiss();
            }
        });

        sliders[RED] = (SeekBar) view.findViewById(R.id.slider_r);
        sliders[GREEN] = (SeekBar) view.findViewById(R.id.slider_g);
        sliders[BLUE] = (SeekBar) view.findViewById(R.id.slider_b);

        sliders[RED].setOnSeekBarChangeListener(this);
        sliders[GREEN].setOnSeekBarChangeListener(this);
        sliders[BLUE].setOnSeekBarChangeListener(this);

        sliders[RED].setProgress(Color.red(color));
        sliders[GREEN].setProgress(Color.green(color));
        sliders[BLUE].setProgress(Color.blue(color));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int selectedColor = Color.argb(0xff, sliders[RED].getProgress(), sliders[GREEN].getProgress(), sliders[BLUE].getProgress());
        previewDrawable.setColor(selectedColor);
        preview.invalidate();

        hexvalue.setText(String.format("#%06X", (0xFFFFFF & selectedColor)));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // do nothing
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // do nothing
    }


    public interface ColorCallback {
        void onColorSelected(int position, String name, int color);
    }
}

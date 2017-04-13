package infection.application9cv9.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import infection.application9cv9.R;

/**
 * Created by Tu Van Ninh on 4/13/2017.
 */
public class DialogNotificationFragment extends DialogFragment {

    private Button buttonFindPath;
    EditText destination;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View rootView = inflater.inflate(R.layout.dialog_choose_dest, container, false);
        buttonFindPath = (Button) rootView.findViewById(R.id.findPath);
        destination = (EditText) rootView.findViewById(R.id.destination);
        buttonFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialog().dismiss();
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    public String getDest() {
        return destination.getText().toString();
    }
}

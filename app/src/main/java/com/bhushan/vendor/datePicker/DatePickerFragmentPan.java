package com.bhushan.vendor.datePicker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

import static com.bhushan.vendor.fragments.PanFragment.SetDateText;


public class DatePickerFragmentPan extends DialogFragment implements DatePickerDialog.OnDateSetListener {


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(),this,year,month,day);
    }

    @Override

    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

        SetDateText(i,i1+1,i2);
    }
}

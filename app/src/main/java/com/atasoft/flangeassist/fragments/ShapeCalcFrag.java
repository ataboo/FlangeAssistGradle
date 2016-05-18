package com.atasoft.flangeassist.fragments;

import android.os.*;
import android.support.v4.app.*;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.atasoft.flangeassist.*;
import com.atasoft.utilities.*;

import android.text.*;

public class ShapeCalcFrag extends Fragment //implements OnClickListener
{
	private View thisFrag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

        thisFrag = inflater.inflate(R.layout.shape_calc , container, false);
		setupShapeSpinners();
        setEditListeners();
		return thisFrag;
    }

	private void setEditListeners(){
		for(int i=0; i < shapeInArr.length; i++){
			shapeInArr[i].addTextChangedListener(new TextWatcher(){
					@Override
					public void afterTextChanged(Editable s){
						updateShapeCalc();
					}
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after){}
					@Override
					public void onTextChanged(CharSequence s, int start, int count, int after){}
				});
		}
	}

	//--------------------Shape Calc Functions----------------
    private ShapeCalcHold shapeCalc;
	private Spinner shapeTypeSpin;
	private ImageView shapeImage;
	private TextView shapeVolBox;
	private TextView shapeSurfBox;
	private EditText[] shapeInArr;
	private TextView[] shapeLabelArr;
	private void setupShapeSpinners(){
		this.shapeCalc = new ShapeCalcHold();
		this.shapeTypeSpin = (Spinner) thisFrag.findViewById(R.id.shapecalc_typeSpin);
		this.shapeImage = (ImageView) thisFrag.findViewById(R.id.shapecalc_image);
		this.shapeInArr = new EditText[3];
		this.shapeInArr[0] = (EditText) thisFrag.findViewById(R.id.shapecalc_input1);
		this.shapeInArr[1] = (EditText) thisFrag.findViewById(R.id.shapecalc_input2);
		this.shapeInArr[2] = (EditText) thisFrag.findViewById(R.id.shapecalc_input3);
		this.shapeLabelArr = new TextView[3];
		this.shapeLabelArr[0] = (TextView) thisFrag.findViewById(R.id.shapecalc_label1);
		this.shapeLabelArr[1] = (TextView) thisFrag.findViewById(R.id.shapecalc_label2);
		this.shapeLabelArr[2] = (TextView) thisFrag.findViewById(R.id.shapecalc_label3);
		this.shapeVolBox = (TextView) thisFrag.findViewById(R.id.shapecalc_volbox);
		this.shapeSurfBox = (TextView) thisFrag.findViewById(R.id.shapecalc_surfbox);		

		ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, ShapeCalcHold.SHAPE_TYPES);
		shapeTypeSpin.setAdapter(typeAdapter);

		shapeTypeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
					refreshShapeFields();
					updateShapeCalc();
				}
				public void onNothingSelected(AdapterView<?> parent) {}
			});
		refreshShapeFields();
		for(int i=0; i < shapeInArr.length; i++){
			shapeInArr[i].setText("0");
		}

    }

	private String selectedShape;
	private void refreshShapeFields(){
		this.selectedShape = (String) shapeTypeSpin.getSelectedItem();
		String[] labelStrings = shapeCalc.getLabelStrings(selectedShape);
		shapeImage.setImageResource(getImageResource(selectedShape));
		for(int i=0; i < shapeLabelArr.length; i++){
			if(i+1 > labelStrings.length) {
				shapeLabelArr[i].setText("");
				shapeInArr[i].setEnabled(false);
			} else {
				shapeInArr[i].setEnabled(true);
				shapeLabelArr[i].setText(labelStrings[i]);
			}
		}

	}

	//rather put this here and not have to pass resources to shapecalchold
	private int getImageResource(String stringType){
		int type = shapeCalc.getType(stringType);
		int resID;
		switch(type){
			case 0:  //Cylinder
				resID = R.drawable.shape_cyl;
				break;
			case 1:  //Sphere
				resID = R.drawable.shape_sph;
				break;
			case 2:  //Box
				resID = R.drawable.shape_box;
				break;
			case 3:  //Rectangle
				resID = R.drawable.shape_rec;
				break;
			default: //Circle
				resID = R.drawable.shape_circ;
				break;
		}
		return resID;
	}

	private void updateShapeCalc(){
		this.selectedShape = (String) shapeTypeSpin.getSelectedItem();
		double[] vals = new double[]{0d,0d,0d};

		for(int i=0; i < shapeInArr.length; i++){
			vals[i]= getDoubleFromEdit(shapeInArr[i]);
		}
		String[] outputs = shapeCalc.getValues(selectedShape, vals);
		shapeVolBox.setText(outputs[0]);
		shapeSurfBox.setText(outputs[1]);
	}

	private double getDoubleFromEdit(EditText eText){
		double retDouble;
		try{
			retDouble = Double.parseDouble(eText.getText().toString());
		} catch (NumberFormatException e){
			Log.e("UnitConFragment", "Can't parse double in shapeCalc input.");
			return 0d;
		}

		return retDouble;
	}	
}

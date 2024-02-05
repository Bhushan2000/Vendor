package com.bhushan.vendor.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bhushan.vendor.database.DatabaseOperations;
import com.bhushan.vendor.R;
import com.bhushan.vendor.activities.LogInActivity;
import com.bhushan.vendor.sharediPreference.SharedPrefManager;
import com.bhushan.vendor.spineer.CategoryAdapter;
import com.bhushan.vendor.spineer.CategoryItem;
import com.bhushan.vendor.spineer.SubCategoryItem;
import com.bhushan.vendor.spineer.SubcategoryAdapter;
import com.bhushan.vendor.url.Links;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddProductFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public AddProductFragment() {
        // Required empty public constructor
    }


    public static AddProductFragment newInstance(String param1, String param2) {
        AddProductFragment fragment = new AddProductFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private Handler handler;

    private Switch aSwitch, bSwitch;
    private TextView txtAutoID, txtDiscountCalculate, txtGSTINRUPEESCalculate;
    public static TextView txtTotalPrice, txtOriginalPrice, txtDiscountPrice, txtYouSaved, txtTax, txtNoOfPieces;

    private LinearLayout discountLayout, gstLayout, summaryLayout;
    boolean isEnable = false;
    double afterDiscount;

    private Button submit, delete;

    // Product Info
    private EditText productTitle;
    //    private EditText category;
    private ImageView productImage;
    //    private EditText subCategory;
    private Spinner subCategory;
    private Spinner category;
    //pricing info
    private EditText productPrice;
    private EditText productPriceDiscou;
    private EditText productPriceInRupe;
    //Stock info
    private EditText noOfPieces;
    private EditText minCompulsoryStock;
    //General info
    private EditText gstInPercentage;
    private EditText gstInRupees;
    private EditText productDescription;

    String string1;
    String string2;
    private ImageButton addImage;


    private Bitmap bitmap;
    private Uri filePath;
    public static String encodedImageString;
    private Dialog loadingDialogue;

    private TextView tvDate;

    Calendar calander;
    SimpleDateFormat simpledateformat;
    String Date;

    // Data

    String productNameString;
    String categoryString;
    String subCategoryString;
    String productPriceString;
    String productPriceDiscouString;
    String productPriceInRupeString;
    String noOfPiecesString;
    String minCompulsoryStockString;
    String gstInPercentageString;
    String gstInRupeesString;
    String productDescriptionString;
    String txtYouSavedString;
    String txtTotalPriceStrinng;


    private TextInputLayout tilProductName;
    private TextInputLayout tilProductPrice;
    private TextInputLayout tilProductPriceDiscount;
    private TextInputLayout tilProductPriceRupees;
    private TextInputLayout tilNoOfPieces;
    private TextInputLayout tilProductMinCompulsoryStock;
    private TextInputLayout tilProductGstInPercentage;
    private TextInputLayout tilProductGstInRupees;
    private TextInputLayout tilProductDescription;
    private TextInputLayout tilQuantity;


    Uri imageUri;

    File imageFile;


    // Spinner Category

    private ArrayList<CategoryItem> categoryList;
    private CategoryAdapter categoryAdapter;
    public static String clickedCategoryName;
    int categoryId;
    boolean selectedCategory = false;


    //Spinner Subcategory
    private ArrayList<SubCategoryItem> subCategoryItemArrayList;
    private SubcategoryAdapter subcategoryAdapter;
    public static String clickedSubCategoryName;
    int subCategoryId;


    //   Quantity
    private EditText Quantity;

    // spinner sQuantityUnit
    private Spinner sQuantityUnit;


    private Spinner sPopular;

    int hold;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);

        if (!SharedPrefManager.getInstance(getContext()).isLoggedIn()) {
            getActivity().finish();
            startActivity(new Intent(getContext(), LogInActivity.class));
        }

        ///////////////////////////////////loading dialogue/////////////////////////////////////////////

        loadingDialogue = new Dialog(getContext());
        loadingDialogue.setContentView(R.layout.loadingprogressdialogue);
        loadingDialogue.setCancelable(false);
        loadingDialogue.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialogue.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        /////////////////////////////////////loading dialogue/////////////////////////////////////////////


        tilProductName = view.findViewById(R.id.tilProductTitle);
        tilProductPrice = view.findViewById(R.id.tilProductPrice);
        tilProductPriceDiscount = view.findViewById(R.id.tilproductPriceInDiscount);
        tilProductPriceRupees = view.findViewById(R.id.tilproductPriceDiscountInRupees);
        tilNoOfPieces = view.findViewById(R.id.tilNoOfPieces);
        tilProductMinCompulsoryStock = view.findViewById(R.id.tilMinCompulsoryStock);
        tilProductGstInPercentage = view.findViewById(R.id.tilGstInPercentage);
        tilProductGstInRupees = view.findViewById(R.id.tilGstInRupees);
        tilProductDescription = view.findViewById(R.id.tilProductDescription);
        tilQuantity = view.findViewById(R.id.tilQuantity);


        // Product Info
        productTitle = view.findViewById(R.id.productTitle);
        //  category = findViewById(R.id.category);
        productImage = view.findViewById(R.id.productImage);

        // subCategory = findViewById(R.id.subCategory);

        category = view.findViewById(R.id.category);
        subCategory = view.findViewById(R.id.subCategory);
        //pricing info
        productPrice = view.findViewById(R.id.product_price);
        productPriceDiscou = view.findViewById(R.id.productPriceInDiscount);
        productPriceInRupe = view.findViewById(R.id.productPriceDiscountInRupees);
        //Stock info
        noOfPieces = view.findViewById(R.id.noOfPieces);
        minCompulsoryStock = view.findViewById(R.id.minCompulsoryStock);
        //General info
        gstInPercentage = view.findViewById(R.id.gstInPercentage);
        gstInRupees = view.findViewById(R.id.gstInRupees);
        productDescription = view.findViewById(R.id.productDescription);


//        delete = view.findViewById(R.id.delete);

        //        it is only use for deleting the product
        //        id = findViewById(R.id.id);


        submit = view.findViewById(R.id.submit);

        addImage = view.findViewById(R.id.addImage);
        aSwitch = view.findViewById(R.id.switch1);
        bSwitch = view.findViewById(R.id.switch2);

//        txtAutoID = findViewById(R.id.txtAutoID);

        txtTotalPrice = view.findViewById(R.id.txtTotalPrice);

        txtOriginalPrice = view.findViewById(R.id.txtOriginalPrice);
        txtDiscountPrice = view.findViewById(R.id.txtDiscountPrice);
        discountLayout = view.findViewById(R.id.discountLayout);
        gstLayout = view.findViewById(R.id.gstLayout);
        summaryLayout = view.findViewById(R.id.summaryLayout);
        txtYouSaved = view.findViewById(R.id.txtDiscountFee);
        txtTax = view.findViewById(R.id.txtTax);


        calander = Calendar.getInstance();
        simpledateformat = new SimpleDateFormat("yyyy-MM-dd");
        Date = simpledateformat.format(calander.getTime());


        categoryList = new ArrayList<>();
        subCategoryItemArrayList = new ArrayList<>();


        txtNoOfPieces = view.findViewById(R.id.txtNoOfPieces);

        productPriceDiscou.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);


        gstInPercentage.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImageDialog();

            }
        });

        Quantity = view.findViewById(R.id.etQuantity);
        sQuantityUnit = view.findViewById(R.id.sQuantityUnit);
        sPopular = view.findViewById(R.id.sPopular);


        sPopular.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hold = sPopular.getSelectedItemPosition();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        sQuantityUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String string = (String) sQuantityUnit.getItemAtPosition(sQuantityUnit.getSelectedItemPosition());


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // Check spinner values
//
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CategoryItem clickedItem = (CategoryItem) parent.getItemAtPosition(position);
                clickedCategoryName = clickedItem.getCategoryName();
                selectedCategory = true;
                if (selectedCategory) {
                    categoryId = clickedItem.getCategoryId();
                    loadCSubcategory();
                    selectedCategory = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        subCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                SubCategoryItem subCategoryItem = (SubCategoryItem) parent.getItemAtPosition(position);
                clickedSubCategoryName = subCategoryItem.getSubCategoryName();
                subCategoryId = subCategoryItem.getSubCategoryId();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isEnable = true;
                    discountLayout.setVisibility(View.VISIBLE);
                    summaryLayout.setVisibility(View.VISIBLE);
                } else {
                    isEnable = false;
                    summaryLayout.setVisibility(View.GONE);
                    discountLayout.setVisibility(View.GONE);
                }
            }
        });
        bSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    gstLayout.setVisibility(View.VISIBLE);
                    summaryLayout.setVisibility(View.VISIBLE);

                } else {

                    gstLayout.setVisibility(View.GONE);
                    summaryLayout.setVisibility(View.GONE);
                }
            }
        });


        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadCategory();
            }
        }, 2000);


        calculations();

        addData();

        return view;

    }

    private boolean validateProductName() {
        String inputName = tilProductName.getEditText().getText().toString().trim();

        if (inputName.isEmpty()) {
            tilProductName.setError("field can't be empty");
            return false;
        } else {
            tilProductName.setError(null);
            return true;

        }
    }

    private boolean validateProductPrice() {
        String inputProductPrice = tilProductPrice.getEditText().getText().toString().trim();

        if (inputProductPrice.isEmpty()) {
            tilProductPrice.setError("field can't be empty");
            return false;
        } else {
            tilProductPrice.setError(null);
            return true;

        }
    }

    private boolean validateProductPriceDiscount() {
        String inputProductPriceDiscount = tilProductPriceDiscount.getEditText().getText().toString().trim();

        if (inputProductPriceDiscount.isEmpty()) {
            tilProductPriceDiscount.setError("field can't be empty");
            return false;
        } else {
            tilProductPriceDiscount.setError(null);
            return true;

        }
    }

    private boolean validateProductPriceRupees() {
        String inputProductPriceRupees = tilProductPriceRupees.getEditText().getText().toString().trim();

        if (inputProductPriceRupees.isEmpty()) {
            tilProductPriceRupees.setError("field can't be empty");
            return false;
        } else {
            tilProductPriceRupees.setError(null);
            return true;

        }
    }

    private boolean validateProductNoOfPieces() {
        String inputProductNoOfPieces = tilNoOfPieces.getEditText().getText().toString().trim();

        if (inputProductNoOfPieces.isEmpty()) {
            tilNoOfPieces.setError("field can't be empty");
            return false;
        } else {
            tilNoOfPieces.setError(null);
            return true;

        }
    }

    private boolean validateProductMinCompulsoryStock() {
        String inputProductMinCompulsoryStock = tilProductMinCompulsoryStock.getEditText().getText().toString().trim();

        if (inputProductMinCompulsoryStock.isEmpty()) {
            tilProductMinCompulsoryStock.setError("field can't be empty");
            return false;
        } else {
            tilProductMinCompulsoryStock.setError(null);
            return true;

        }
    }

    private boolean validateProductGSTPercentage() {
        String inputProductGstInPercentage = tilProductGstInPercentage.getEditText().getText().toString().trim();

        if (inputProductGstInPercentage.isEmpty()) {
            tilProductGstInPercentage.setError("field can't be empty");
            return false;
        } else {
            tilProductGstInPercentage.setError(null);
            return true;

        }
    }

    private boolean validateProductGSTRupees() {
        String inputProductGstInRupees = tilProductGstInRupees.getEditText().getText().toString().trim();

        if (inputProductGstInRupees.isEmpty()) {
            tilProductGstInRupees.setError("field can't be empty");
            return false;
        } else {
            tilProductGstInRupees.setError(null);
            return true;

        }
    }

    private boolean validateProductDescription() {
        String inputProductDescription = tilProductDescription.getEditText().getText().toString().trim();

        if (inputProductDescription.isEmpty()) {
            tilProductDescription.setError("field can't be empty");
            return false;
        } else {
            tilProductDescription.setError(null);
            return true;

        }
    }


    private boolean validateProductQuantity() {
        String inputProductQuantity = tilQuantity.getEditText().getText().toString().trim();

        if (inputProductQuantity.isEmpty()) {
            tilQuantity.setError("field can't be empty");
            return false;
        } else {
            tilQuantity.setError(null);
            return true;

        }
    }

    private void loadCategory() {

        loadingDialogue.show();
        AndroidNetworking.get(Links.loadCategory)
                .setTag("MYSQL_UPLOAD")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        loadingDialogue.dismiss();

                        try {
                            // converting the string to json array object
                            JSONArray array = new JSONArray(response);


                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);

                                int id = product.getInt("id");
                                String catname = product.getString("catname");
                                String catimg = product.getString("catimg");


                                categoryList.add(new CategoryItem(catname, catimg, id));
                                categoryAdapter = new CategoryAdapter(getContext(), categoryList);
                                category.setAdapter(categoryAdapter);
                                categoryAdapter.notifyDataSetChanged();
                            }


                        } catch (JSONException e) {

                            loadingDialogue.dismiss();
                            e.printStackTrace();
                            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                        }


                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        loadingDialogue.dismiss();
                        Toast.makeText(getContext(), "UNSUCCESSFUL :  ERROR IS : n" + anError.getMessage(), Toast.LENGTH_LONG).show();


                    }
                });


    }

    private void loadCSubcategory() {
        loadingDialogue.show();
        subCategoryItemArrayList.clear();

        AndroidNetworking.upload(Links.loadSubcategory)
                .addMultipartParameter("id", String.valueOf(categoryId))
                .setTag("MYSQL_UPLOAD")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        loadingDialogue.dismiss();


                        try {
                            // converting the string to json array object
                            JSONArray array = new JSONArray(response);


                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);

                                int id = product.getInt("id");
                                String cat_id = product.getString("cat_id");
                                String name = product.getString("name");
                                String img = product.getString("img");

                                subCategoryItemArrayList.add(new SubCategoryItem(id, cat_id, name, img));
                                subcategoryAdapter = new SubcategoryAdapter(getContext(), subCategoryItemArrayList);
                                subCategory.setAdapter(subcategoryAdapter);

                                subcategoryAdapter.notifyDataSetChanged();
                            }


                        } catch (JSONException e) {
                            loadingDialogue.dismiss();

                            e.printStackTrace();
                            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                        }


                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        loadingDialogue.dismiss();
                        Toast.makeText(getContext(), "UNSUCCESSFUL :  ERROR IS : n" + anError.getMessage(), Toast.LENGTH_LONG).show();


                    }
                });

    }

    public void addData() {

        submit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                if (!validateProductName() |
                        !validateProductPrice() |
                        !validateProductPriceDiscount() |

                        !validateProductPriceRupees() |
                        !validateProductNoOfPieces() |

                        !validateProductMinCompulsoryStock() |
                        !validateProductGSTPercentage() |

                        !validateProductGSTRupees() |
                        !validateProductDescription() |
                        !validateProductQuantity()
                ) {
                    return;
                }

                add();


            }
        });


    }


    private void calculations() {


        productPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                productPriceInRupe.setText(String.valueOf(discountCalculate()));
                double prprice = Double.parseDouble(productPrice.getText().toString());
                prprice = Double.parseDouble(new DecimalFormat("##.##").format(prprice));


                txtOriginalPrice.setText("₹ " + String.valueOf(prprice));


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        productPriceDiscou.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                productPriceInRupe.setText(String.valueOf(discountCalculate()));
                double prprice = Double.parseDouble(productPrice.getText().toString());
                // discounted rupees
                double discountfee = prprice - discountCalculate();
                discountfee = Double.parseDouble(new DecimalFormat("##.##").format(discountfee));
                txtYouSaved.setText(String.valueOf(discountfee));

                double proPriceAfterDiscount = Double.parseDouble(productPrice.getText().toString()) - discountfee;
                proPriceAfterDiscount = Double.parseDouble(new DecimalFormat("##.##").format(proPriceAfterDiscount));

                txtDiscountPrice.setText("+ ₹ " + String.valueOf(proPriceAfterDiscount));


            }

            public void afterTextChanged(Editable s) {

            }
        });


        gstInPercentage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                gstInRupees.setText(String.valueOf(calculateGST()));
                txtTax.setText("+ ₹ " + String.valueOf(calculateGST()));
                double tax = Double.valueOf(gstInRupees.getText().toString());
                double total = Double.valueOf(productPriceInRupe.getText().toString());
                tax = Double.parseDouble(new DecimalFormat("##.##").format(tax));
                total = Double.parseDouble(new DecimalFormat("##.##").format(total));

                txtTotalPrice.setText(String.valueOf(tax + total));


            }

            public void afterTextChanged(Editable s) {

            }
        });


        minCompulsoryStock.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtNoOfPieces.setText(noOfPieces.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private double calculateGST() {
        double Amount = 0;
        double gst = 0;
        if (! productPrice.getText().toString().equals("")) {
            Amount = Double.parseDouble(productPrice.getText().toString());
            Amount = Double.parseDouble(new DecimalFormat("##.##").format(Amount));

        } else {
            Amount = 0;

        }
        if (! gstInPercentage.getText().toString().equals("")) {
            gst = Double.parseDouble(gstInPercentage.getText().toString());
            gst = Double.parseDouble(new DecimalFormat("##.##").format(gst));

        } else {
            gst = 0;
        }


        double div = (Amount * gst) / 100;
        div = Double.parseDouble(new DecimalFormat("##.##").format(div));

        return div;

    }

    private double discountCalculate() {
        double bill = 0;
        double discount = 0;


        if (! productPrice.getText().toString().equals("") && productPrice.getText().length() > 0) {
            bill = Double.parseDouble(productPrice.getText().toString());
            bill = Double.parseDouble(new DecimalFormat("##.##").format(bill));

        } else {
            bill = 0;

        }
        if (productPriceDiscou.getText().toString() != "" && productPriceDiscou.getText().length() > 0) {
            discount = Double.parseDouble(productPriceDiscou.getText().toString());
            discount = Double.parseDouble(new DecimalFormat("##.##").format(discount));

        } else {
            discount = 0;

        }

        afterDiscount = Double.parseDouble(new DecimalFormat("##.##").format(afterDiscount));

        return afterDiscount = (bill - (bill * discount / 100));
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void addImageDialog() {
        String options[] = {"Select a photo from gallery"};
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle("Choose Action");
        builder.setIcon(R.drawable.ic_baseline_image_24);
        builder.setBackground(getResources().getDrawable(R.drawable.slider_background, null));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
             if (which == 0) {
                    Intent gallery = new Intent();
                    gallery.setAction(Intent.ACTION_GET_CONTENT);
                    gallery.setType("image/*");
                    startActivityForResult(Intent.createChooser(gallery, "Select Picture"), 1);
                }
            }
        });

        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                productImage.setImageBitmap(bitmap);
                encodeBitmapImage(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public String getImagePath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) {
            return null;
        }
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(columnIndex);
        cursor.close();
        return s;
    }

    public boolean isNetWorkAvailable() {
        // Internet Connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();


        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;

        } else {
            return false;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void minStockLessThanStock() {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle("Stock Alert");
        builder.setBackground(getResources().getDrawable(R.drawable.slider_background, null));

        builder.setMessage("Minimum compulsory stock less than No  of Pieces ");
        builder.setCancelable(false);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                if ((Integer.parseInt(minCompulsoryStock.getText().toString()) < Integer.parseInt(noOfPieces.getText().toString()))) {
                    Toast.makeText(getContext(), Integer.parseInt(minCompulsoryStock.getText().toString()) + "  is less than " + Integer.parseInt(noOfPieces.getText().toString()), Toast.LENGTH_SHORT).show();

                } else if ((Integer.parseInt(minCompulsoryStock.getText().toString()) > Integer.parseInt(noOfPieces.getText().toString()))) {
                    Toast.makeText(getContext(), Integer.parseInt(minCompulsoryStock.getText().toString()) + "  is greater than " + Integer.parseInt(noOfPieces.getText().toString()), Toast.LENGTH_SHORT).show();

                } else if ((Integer.parseInt(minCompulsoryStock.getText().toString()) == Integer.parseInt(noOfPieces.getText().toString()))) {
                    Toast.makeText(getContext(), Integer.parseInt(minCompulsoryStock.getText().toString()) + "  is equal to  " + Integer.parseInt(noOfPieces.getText().toString()), Toast.LENGTH_SHORT).show();

                }


            }
        });
        //Creating dialog box
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    //method to convert the selected image to base64 encoded string

    public static void encodeBitmapImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        encodedImageString = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);

    }

    private void add() {

        productNameString = productTitle.getText().toString();
        categoryString = category.getSelectedItem().toString();
        subCategoryString = subCategory.getSelectedItem().toString();
        imageFile = new File(getImagePath(filePath));
        productPriceString = productPrice.getText().toString();
        productPriceDiscouString = productPriceDiscou.getText().toString();
        productPriceInRupeString = productPriceInRupe.getText().toString();
        noOfPiecesString = noOfPieces.getText().toString();
        minCompulsoryStockString = minCompulsoryStock.getText().toString();
        gstInPercentageString = gstInPercentage.getText().toString();
        gstInRupeesString = gstInRupees.getText().toString();
        txtYouSavedString = txtYouSaved.getText().toString();
        txtTotalPriceStrinng = txtTotalPrice.getText().toString();
        productDescriptionString = productDescription.getText().toString();
        String quantityString = Quantity.getText().toString();
        String quantityUnitString = sQuantityUnit.getSelectedItem().toString();
        final String imageString = encodedImageString;


       String user_id =   SharedPrefManager.getInstance(getContext()).getUserID();


        loadingDialogue.show();
        AndroidNetworking.upload(Links.productUpload)
                .addMultipartParameter("pName", productNameString)
                .addMultipartParameter("pCategory", String.valueOf(categoryId))
                .addMultipartParameter("pSubCategory", String.valueOf(subCategoryId))
                .addMultipartFile("image", imageFile)
                .addMultipartParameter("pOriginalPrice", productPriceString)
                .addMultipartParameter("discountPercent", productPriceDiscouString)
                .addMultipartParameter("discountRupees", productPriceInRupeString)
                .addMultipartParameter("noOfPices", noOfPiecesString)
                .addMultipartParameter("minStock", minCompulsoryStockString)
                .addMultipartParameter("gstPercent", gstInPercentageString)
                .addMultipartParameter("gstRupees", gstInRupeesString)
                .addMultipartParameter("savedRupees", txtYouSavedString)
                .addMultipartParameter("pTotalPrice", txtTotalPriceStrinng)
                .addMultipartParameter("pDescription", productDescriptionString)
                .addMultipartParameter("pStatus", String.valueOf(1))
                .addMultipartParameter("quantity", quantityString + quantityUnitString)
                .addMultipartParameter("popular", String.valueOf(hold))
                .addMultipartParameter("vid", user_id)


                .setTag("MYSQL_UPLOAD")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null) {
                            try {
                                loadingDialogue.dismiss();

                                //SHOW RESPONSE FROM SERVER

                                JSONObject jObj = new JSONObject(response);
                                boolean error = jObj.getBoolean("error");
                                // Check for error node in json
                                if (!error) {

                                    String message = jObj.getString("message");
                                    String image = jObj.getString("image");
                                    String title = jObj.getString("title");

                                    DatabaseOperations.sendSinglePush(getContext(),title,message,image,loadingDialogue);
                                    DatabaseOperations.insertNoti(getContext(),title,imageString,message,loadingDialogue);


                                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

                                }


                            } catch (Exception e) {
                                loadingDialogue.dismiss();

                                e.printStackTrace();
                                Toast.makeText(getContext(), "JSONException " + e.getMessage(), Toast.LENGTH_LONG).show();

                            }

                            //RESET VIEWS
//
                            productTitle.setText("");
                            productImage.setImageResource(R.drawable.ic_baseline_image_24);

                            // error here
//                                productPrice.setText("");
//                                productPriceInRupe.setText("");


                            noOfPieces.setText("");
                            minCompulsoryStock.setText("");
                            //      gstInPercentage.setText("");
                            gstInRupees.setText("");
                            txtTotalPrice.setText("");
                            txtYouSaved.setText("");
                            txtTax.setText("");
                            txtOriginalPrice.setText("");
                            txtDiscountPrice.setText("");
                            txtNoOfPieces.setText("");
                            productDescription.setText("");


                        } else {
                            loadingDialogue.dismiss();

                            Toast.makeText(getContext(), "NULL RESPONSE. ", Toast.LENGTH_LONG).show();

                        }
                        loadingDialogue.dismiss();
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        loadingDialogue.dismiss();
                        Toast.makeText(getContext(), "UNSUCCESSFUL :  ERROR IS : n" + anError.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
    }


}
package com.ehsanmashhadi.library.view;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ehsanmashhadi.library.R;
import com.ehsanmashhadi.library.model.Country;

import java.util.List;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnCountryClickListener {
        void onCountrySelected(Country country);
    }

    private List<Country> mCountryList;
    private boolean isEmpty;
    private OnCountryClickListener mOnCountryClickListener;
    private static boolean sShowingFlag;
    private static boolean sShowingDialCode;
    private static String sPreselectCountry;

    RecyclerViewAdapter(List<Country> countryList, String preselectCountry, boolean showingFlag, boolean showingDialCode) {

        mCountryList = countryList;
        sShowingFlag = showingFlag;
        sShowingDialCode = showingDialCode;
        sPreselectCountry = preselectCountry;
    }

    void setListener(OnCountryClickListener onCountryClickListener) {

        mOnCountryClickListener = onCountryClickListener;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == 0) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_country_empty, parent, false);

            return new CountryHolderEmpty(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_country, parent, false);

            return new CountryHolder(itemView, position -> {
                mOnCountryClickListener.onCountrySelected(mCountryList.get(position));
                itemView.setSelected(true);
                sPreselectCountry = mCountryList.get(position).getName();
                notifyDataSetChanged();
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Country country = mCountryList.get(position);
        if (isEmpty) {
            bindingViewHolderEmpty();
        } else {
            bindingViewHolder((CountryHolder) holder, country);
        }
    }

    private void bindingViewHolder(CountryHolder holder, Country country) {
        holder.mTextViewName.setText(country.getName());
        holder.mTextViewCode.setText(country.getDialCode());


        int resourceId = holder.itemView.getContext().getResources().getIdentifier(country.getFlagName(), "drawable", holder.itemView.getContext().getPackageName());

        holder.mImageViewFlag.setImageResource(resourceId);
        if (sPreselectCountry != null)
            if (country.getName().toLowerCase().equals(sPreselectCountry.toLowerCase())) {
                holder.itemView.setSelected(true);
                //Not setting background in XML because of not supporting reference attributes in pre-lollipop Android version
                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = holder.itemView.getContext().getTheme();
                theme.resolveAttribute(R.attr.rowBackgroundSelectedColor, typedValue, true);
                @ColorInt int color = typedValue.data;
                holder.itemView.setBackgroundColor(color);

            } else {
                holder.itemView.setSelected(false);
                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = holder.itemView.getContext().getTheme();
                theme.resolveAttribute(R.attr.rowBackgroundColor, typedValue, true);
                @ColorInt int color = typedValue.data;
                holder.itemView.setBackgroundColor(color);
            }
        if (sShowingFlag)
            holder.mImageViewFlag.setVisibility(View.VISIBLE);
        else
            holder.mImageViewFlag.setVisibility(View.GONE);
    }

    private void bindingViewHolderEmpty() {
    }

    @Override
    public int getItemCount() {
        return mCountryList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isEmpty) {
            return 0;
        } else {
            return 1;
        }
    }

    void setCountries(List<Country> countries, boolean isEmpty) {
        this.isEmpty = isEmpty;
        mCountryList = countries;
        notifyDataSetChanged();
    }

    static class CountryHolder extends RecyclerView.ViewHolder {

        private interface OnItemClickListener {
            void onItemSelected(int position);
        }

        private TextView mTextViewName;
        private TextView mTextViewCode;
        private ImageView mImageViewFlag;

        CountryHolder(@NonNull View itemView, final OnItemClickListener onItemClickListener) {

            super(itemView);

            mTextViewName = itemView.findViewById(R.id.textview_name);
            mTextViewCode = itemView.findViewById(R.id.textview_code);
            mImageViewFlag = itemView.findViewById(R.id.imageview_flag);

            if (sShowingFlag) {
                mImageViewFlag.setVisibility(View.GONE);
            }
            if (!sShowingDialCode) {
                mTextViewCode.setVisibility(View.GONE);
            }
            itemView.setOnClickListener(v -> onItemClickListener.onItemSelected(getAdapterPosition()));
        }
    }

    static class CountryHolderEmpty extends RecyclerView.ViewHolder {

        CountryHolderEmpty(@NonNull View itemView) {
            super(itemView);
        }
    }
}

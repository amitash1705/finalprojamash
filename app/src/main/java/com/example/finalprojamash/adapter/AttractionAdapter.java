package com.example.finalprojamash.adapter;

// מייבא כלי שמאפשר לנו להציג רשימה בצורה ממוטבת (RecyclerView)
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

// אנוטציה שמוודאת שהקוד נכון בזמן קומפילציה
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojamash.R;
import com.example.finalprojamash.model.Attraction;
import com.example.finalprojamash.utils.ImageUtil;

import java.util.List;

/// Adapter זה מחבר בין הנתונים (Attraction) לבין איך שהם מוצגים במסך (RecyclerView)
public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.ViewHolder> {

    /// ממשק שמגדיר "מה קורה בלחיצה על אטרקציה"
    /// כלומר מי שמשתמש באדפטר הזה יחליט מה לעשות בלחיצה
    public interface OnAttrctionClickListener {
        void onAttractionClick(Attraction attraction);        // לחיצה רגילה
        void onLongAttractionClick(Attraction attraction);    // לחיצה ארוכה (מחיקה/ביטול וכו׳)
    }

    /// אובייקט שמטפל באירועים של לחיצות (מגיע מה-Activity)
    private final OnAttrctionClickListener onAttrctionClickListener;

    /// הרשימה של כל האטרקציות שיוצגו במסך
    /// כל תא ברשימה = אובייקט מסוג Attraction
    private final List<Attraction> attractionList;

    /// בנאי (Constructor)
    /// מקבל:
    /// 1. רשימת אטרקציות
    /// 2. מאזין ללחיצות על פריטים
    public AttractionAdapter(List<Attraction> attractionList,
                             OnAttrctionClickListener onAttrctionClickListener) {
        this.onAttrctionClickListener = onAttrctionClickListener;
        this.attractionList = attractionList;
    }

    /// יצירת View חדש (כל שורה ברשימה)
    /// כאן Android "מייצר" את העיצוב של כל אייטם ברשימה
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        /// הופך קובץ XML (עיצוב של שורה אחת) ל־View אמיתי
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.one_attraction, parent, false);

        /// מחזיר ViewHolder שמחזיק את כל הרכיבים של השורה
        return new ViewHolder(view);
    }

    /// חיבור בין נתון אמיתי לבין UI
    /// כאן קורה כל "הציור" של הנתונים על המסך
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        /// לוקחים את האובייקט הנוכחי מהרשימה לפי מיקום
        Attraction attraction = attractionList.get(position);

        /// בדיקת בטיחות - אם אין נתון, לא עושים כלום
        if (attraction == null) return;

        /// הצגת שם האטרקציה בטקסט
        holder.attractionNameTextView.setText(attraction.getName());

        /// הצגת העיר של האטרקציה
        holder.tvAttCity.setText(attraction.getCity());

        /// הצגת המדינה של האטרקציה
        holder.tvAttCountry.setText(attraction.getCountry());

        /// המרת תמונה מ־Base64 ל־Bitmap והצגתה
        /// (זה קורה כי התמונה נשמרה כטקסט בבסיס נתונים)
        holder.attractionImageView.setImageBitmap(
                ImageUtil.convertFrom64base(attraction.getPic())
        );

        /// לחיצה רגילה על פריט ברשימה
        holder.itemView.setOnClickListener(v -> {
            if (onAttrctionClickListener != null) {
                onAttrctionClickListener.onAttractionClick(attraction);
            }
        });

        /// לחיצה ארוכה על פריט ברשימה
        /// בדרך כלל משמש למחיקה או עריכה
        holder.itemView.setOnLongClickListener(v -> {
            if (onAttrctionClickListener != null) {
                onAttrctionClickListener.onLongAttractionClick(attraction);
            }
            return true; // אומר למערכת "טיפלנו בלחיצה"
        });
    }

    /// מחזיר כמה פריטים יש ברשימה
    /// RecyclerView משתמש בזה כדי לדעת כמה שורות לצייר
    @Override
    public int getItemCount() {
        return attractionList.size();
    }

    /// מחלקה פנימית שמייצגת "שורה אחת במסך"
    /// היא מחזיקה את כל ה-Views של אותו פריט
    public static class ViewHolder extends RecyclerView.ViewHolder {

        /// שם האטרקציה
        public final TextView attractionNameTextView;

        /// מדינה
        public final TextView tvAttCountry;

        /// עיר
        public final TextView tvAttCity;

        /// תמונה
        public final ImageView attractionImageView;

        /// בנאי שמקשר בין ה-XML לבין המשתנים פה
        public ViewHolder(View itemView) {
            super(itemView);

            /// חיבור בין ID מה-XML לבין משתנה בקוד
            attractionNameTextView = itemView.findViewById(R.id.tvAttName);
            tvAttCountry = itemView.findViewById(R.id.tvAttCountry);
            tvAttCity = itemView.findViewById(R.id.tvAttCity);
            attractionImageView = itemView.findViewById(R.id.ivAttPic);
        }
    }
}
package com.example.finalprojamash.adapter;
// מאפשר להשתמש ב-INVISIBLE כדי להסתיר כפתורים
import static android.view.View.INVISIBLE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojamash.Loginamash;
import com.example.finalprojamash.R;
import com.example.finalprojamash.model.Travel;
import com.example.finalprojamash.utils.ImageUtil;

import java.util.List;

/// Adapter שמציג רשימת טיולים במסך (RecyclerView)
/// כל שורה = טיול אחד (Travel)
public class TravelAdapter extends RecyclerView.Adapter<TravelAdapter.TravelViewHolder> {

    /// הקשר למסך (Context) – נותן גישה למשאבים של Android
    private Context context;

    /// הרשימה של כל הטיולים שמוצגים במסך
    private List<Travel> travelList;

    /// מאזין ללחיצות (עריכה / מחיקה / לחיצה על פריט)
    private OnTravelClickListener listener;

    /// ממשק שמגדיר מה קורה כשמשתמש לוחץ על פריט בטיול
    public interface OnTravelClickListener {
        void onEditClick(Travel travel, int position);    // עריכת טיול
        void onDeleteClick(Travel travel, int position);  // מחיקת טיול
        void onItemClick(Travel travel, int position);    // לחיצה רגילה
    }

    /// בנאי של האדפטר
    /// מקבל:
    /// 1. context - המסך שבו זה מוצג
    /// 2. רשימת טיולים
    /// 3. מאזין ללחיצות
    public TravelAdapter(Context context,
                         List<Travel> travelList,
                         OnTravelClickListener listener) {

        this.context = context;
        this.travelList = travelList;
        this.listener = listener;
    }

    /// יצירת View חדש (שורה אחת ברשימה)
    /// ממיר XML של פריט טיול ל-View אמיתי
    @NonNull
    @Override
    public TravelViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                               int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.itemtravelamash, parent, false);

        return new TravelViewHolder(view);
    }

    /// חיבור בין נתונים לבין UI
    /// כאן כל טיול "נכנס למסך"
    @Override
    public void onBindViewHolder(@NonNull TravelViewHolder holder,
                                 int position) {

        /// לוקחים טיול לפי מיקום
        Travel travel = travelList.get(position);

        /// הצגת שם הטיול / יעד
        holder.tvDestination.setText(travel.getName());

        /// הצגת פרטים של הטיול
        holder.tvDetails.setText(travel.getDetails());

        /// הצגת תמונה של הטיול
        /// לוקחים את האטרקציה הראשונה ברשימה ומציגים את התמונה שלה
        holder.ivTravelPic.setImageBitmap(
                ImageUtil.convertFrom64base(
                        travel.getAttractionList().get(0).getPic()
                )
        );

        /// לחיצה על כפתור עריכה
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(travel, position);
            }
        });

        /// לחיצה על כפתור מחיקה
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(travel, position);
            }
        });

        /// לחיצה על כל השורה (הטיול עצמו)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(travel, position);
            }
        });
    }

    /// כמה פריטים יש ברשימה
    @Override
    public int getItemCount() {
        return travelList.size();
    }

    /// מחלקה שמחזיקה את כל ה-Views של שורה אחת
    public static class TravelViewHolder extends RecyclerView.ViewHolder {

        ImageView ivTravelPic;     // תמונה של הטיול
        TextView tvDestination;    // שם הטיול
        TextView tvDetails;        // פרטים
        ImageButton btnEdit;       // כפתור עריכה
        ImageButton btnDelete;     // כפתור מחיקה

        public TravelViewHolder(@NonNull View itemView) {
            super(itemView);

            /// חיבור בין XML לבין משתנים בקוד
            tvDestination = itemView.findViewById(R.id.tvTripDestination);
            tvDetails = itemView.findViewById(R.id.tvTripDetails);
            btnEdit = itemView.findViewById(R.id.btnEditTrip);
            btnDelete = itemView.findViewById(R.id.btnDeleteTrip);
            ivTravelPic = itemView.findViewById(R.id.ivTripPic);

            /// 🔥 לוגיקה חשובה: בדיקת הרשאות Admin
            /// אם המשתמש הוא Admin → מסתירים כפתורי עריכה ומחיקה
            if (Loginamash.isAdmin) {

                btnEdit.setVisibility(INVISIBLE);
                btnDelete.setVisibility(INVISIBLE);
            }
        }
    }
}
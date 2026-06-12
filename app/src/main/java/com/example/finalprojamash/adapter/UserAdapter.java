package com.example.finalprojamash.adapter;
// RecyclerView - רשימה חכמה שממחזרת Views כדי לחסוך זיכרון וביצועים
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojamash.R;
import com.example.finalprojamash.model.User;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

/// Adapter שמציג רשימת משתמשים במסך (Users List)
/// כל פריט = משתמש אחד (User)
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    /// ממשק שמגדיר מה קורה כשמשתמש לוחץ על פריט
    public interface OnUserClickListener {
        void onUserClick(User user);        // לחיצה רגילה
        void onLongUserClick(User user);    // לחיצה ארוכה (לרוב מחיקה)
    }

    /// הרשימה הפנימית של המשתמשים שמוצגים במסך
    /// חשוב: זו הרשימה שה-Adapter "שולט עליה"
    private final List<User> userList;

    /// מאזין ללחיצות שמגיע מה-Activity
    private final OnUserClickListener onUserClickListener;

    /// בנאי של האדפטר
    /// מקבל רק listener, והרשימה נוצרת ריקה בפנים
    public UserAdapter(@Nullable final OnUserClickListener onUserClickListener) {

        /// יצירת רשימה ריקה (בהתחלה אין נתונים)
        userList = new ArrayList<>();

        /// שמירת המאזין ללחיצות
        this.onUserClickListener = onUserClickListener;
    }

    /// יצירת שורה חדשה (ViewHolder חדש)
    /// מופעל כש-RecyclerView צריך ליצור פריט חדש
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {

        /// המרה של XML של משתמש ל-View אמיתי
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.oneuseramash, parent, false);

        /// עטיפה של ה-View בתוך ViewHolder
        return new ViewHolder(view);
    }

    /// חיבור בין נתונים (User) לבין המסך
    /// כאן כל משתמש מוצג בפועל
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {

        /// שליפת המשתמש הנוכחי מהרשימה
        User user = userList.get(position);

        /// בדיקת בטיחות
        if (user == null) return;

        /// הצגת שם מלא
        holder.tvFullName.setText(user.getFname());

        /// הצגת אימייל
        holder.tvEmail.setText(user.getEmail());

        /// הצגת טלפון
        holder.tvPhone.setText(user.getPhone());

        /// משתנה שנועד לאתחול ראשי תיבות (לא בשימוש כרגע)
        String initials = "";

        /// לחיצה רגילה על משתמש
        holder.itemView.setOnClickListener(v -> {
            if (onUserClickListener != null) {
                onUserClickListener.onUserClick(user);
            }
        });

        /// לחיצה ארוכה (לרוב למחיקה / תפריט פעולות)
        holder.itemView.setOnLongClickListener(v -> {
            if (onUserClickListener != null) {
                onUserClickListener.onLongUserClick(user);
            }
            return true; // אומר: טיפלנו בלחיצה
        });
    }

    /// מחזיר כמה משתמשים יש ברשימה
    @Override
    public int getItemCount() {
        return userList.size();
    }

    /// עדכון מלא של הרשימה מבחוץ (מה-Activity)
    /// מוחק ישן ומכניס חדש
    public void setUserList(List<User> users) {
        userList.clear();
        userList.addAll(users);
        notifyDataSetChanged(); // עדכון כל המסך
    }

    /// הוספת משתמש אחד בלבד לרשימה
    /// שימושי כשנוסף משתמש חדש
    public void addUser(User user) {
        userList.add(user);
        notifyItemInserted(userList.size() - 1);
    }

    /// עדכון משתמש קיים
    /// מחפש אותו ברשימה ומחליף
    public void updateUser(User user) {
        int index = userList.indexOf(user);

        /// אם לא נמצא - יוצא
        if (index == -1) return;

        userList.set(index, user);
        notifyItemChanged(index);
    }

    /// מחיקת משתמש מהרשימה
    public void removeUser(User user) {
        int index = userList.indexOf(user);

        /// אם לא נמצא - לא עושים כלום
        if (index == -1) return;

        userList.remove(index);
        notifyItemRemoved(index);
    }

    /// ViewHolder = מחזיק את כל ה-Views של שורה אחת
    /// זה חוסך יצירה מחדש של Views (שיפור ביצועים)
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvFullName, tvEmail, tvPhone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            /// חיבור בין XML לבין קוד
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvPhone = itemView.findViewById(R.id.tvPhone);
        }
    }
}
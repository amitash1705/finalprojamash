package com.example.finalprojamash.adapter;

// מאפשר לנו להציג רשימה בתוך ListView (לא RecyclerView)
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.finalprojamash.R;
import com.example.finalprojamash.model.ImageSourceOption;

import java.util.List;

/// Adapter זה אחראי על הצגת אפשרויות (גלריה / מצלמה) בתוך BottomSheet או ListView
/// זה לא RecyclerView אלא ArrayAdapter פשוט יותר
public class ImageSourceAdapter extends ArrayAdapter<ImageSourceOption> {

    /// ממשק שמגדיר מה קורה כשבוחרים אפשרות (גלריה / מצלמה)
    public interface OnImageSourceSelectedListener {
        void onImageSourceSelected(ImageSourceOption option);
    }

    /// LayoutInflater = כלי שממיר XML ל־View אמיתי
    private final LayoutInflater inflater;

    /// הרשימה של האפשרויות (גלריה / מצלמה וכו׳)
    private final List<ImageSourceOption> objects;

    /// מאזין ללחיצה על פריט
    private OnImageSourceSelectedListener listener;

    /// בנאי - מקבל:
    /// 1. context (המסך)
    /// 2. רשימת אפשרויות
    /// 3. מה לעשות בלחיצה
    public ImageSourceAdapter(@NonNull Context context,
                              @NonNull List<ImageSourceOption> objects,
                              @NonNull OnImageSourceSelectedListener listener) {

        /// קורא לבנאי של ArrayAdapter (מחייב אותו לעבוד עם ListView)
        super(context, R.layout.item_image_source, objects);

        /// שומר את ה־Inflater כדי ליצור Views
        this.inflater = LayoutInflater.from(context);

        /// שומר את הרשימה
        this.objects = objects;

        /// שומר את המאזין ללחיצות
        this.listener = listener;
    }

    /// מחזיר כמה פריטים יש ברשימה
    /// ListView משתמש בזה כדי לדעת כמה שורות לצייר
    @Override
    public int getCount() {
        return objects.size();
    }

    /// מחזיר פריט לפי מיקום ברשימה
    @Nullable
    @Override
    public ImageSourceOption getItem(int position) {
        return objects.get(position);
    }

    /// זה המקום שבו כל שורה במסך נבנית בפועל
    /// convertView = View ממוחזר (ביצועים טובים יותר)
    @NonNull
    @Override
    public View getView(int position,
                        @Nullable View convertView,
                        @NonNull ViewGroup parent) {

        /// אם אין View ממוחזר → יוצרים חדש מ-XML
        if (convertView == null) {
            convertView = this.inflater.inflate(
                    R.layout.item_image_source,
                    parent,
                    false
            );
        }

        /// חיבור בין רכיבי XML לבין משתנים בקוד
        ImageView icon = convertView.findViewById(R.id.icon_dialog_item);
        TextView title = convertView.findViewById(R.id.text_dialog_item);
        TextView description = convertView.findViewById(R.id.text_dialog_item_description);

        /// לוקחים את האובייקט הנוכחי מהרשימה
        ImageSourceOption item = getItem(position);

        /// בדיקה שלא קיבלנו null
        if (item != null) {

            /// הצגת כותרת (למשל: Gallery / Camera)
            title.setText(item.getTitle());

            /// הצגת הסבר קטן מתחת
            description.setText(item.getDescription());

            /// הצגת אייקון לפי הבחירה
            icon.setImageResource(item.getIconResource());
        }

        /// לחיצה על כל שורה ברשימה
        convertView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onImageSourceSelected(item);
            }
        });

        /// מחזירים את ה-View המוכן ל-ListView
        return convertView;
    }
}
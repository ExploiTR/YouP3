package utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import app.exploitr.nsg.youp3.R;


public class SuperToast extends View {

    public SuperToast(Context context) {
        super(context);
    }

    public static void makeText(Context context, Object message, int duration) {
        Toast toast = new Toast(context);

        WindowManager windowManager =
                (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        @SuppressLint("InflateParams")
        View toastLayout = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.supertoast_layout, null);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        int height = (int) ((displayMetrics.heightPixels) * (0.1));
        int width = (int) (displayMetrics.widthPixels * (0.8));

        TextView lambda = toastLayout.findViewById(R.id.superToast_text);
        lambda.setText(message.toString());
        lambda.setWidth(width);
        lambda.setHeight(height);

        toast.setView(toastLayout);
        toast.setGravity(Gravity.BOTTOM, 0, 200);
        toast.setDuration(duration);
        toast.show();
    }

}

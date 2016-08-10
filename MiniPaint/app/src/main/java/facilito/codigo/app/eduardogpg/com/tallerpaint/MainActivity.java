package facilito.codigo.app.eduardogpg.com.tallerpaint;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import android.widget.LinearLayout.LayoutParams;

public class MainActivity extends AppCompatActivity {

    private DrawView drawView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.drawView = new DrawView(this);

        this.setContentView(this.drawView);
        this.addContentView(this.drawView.row, this.drawView.params);

    }

    public class DrawView extends View{

        private final int finalWidthCircle = 14;
        private final int finalWidthBrush = 12;

        private final int finalColor = Color.parseColor("#9FA9DF");

        private float lastPosX, lastPosY;

        private Paint circlePaint;//Estilos
        private Path circlePath;//Figuras geometricas

        private Paint brushPaint;
        private Path brushPath;

        private Canvas canvas;
        private Bitmap myBitMap;
        private Paint bitMaptPaint;

        private LayoutParams params;
        private LinearLayout row;

        private Button eraser;
        private Button pencil;
        private Button clean;

        public DrawView(Context context){
            super(context);
            this.bitMaptPaint = new Paint();

            this.createCircle();
            this.createBrush();

            this.createButtons(context);

            this.setBackgroundColor( Color.WHITE );
        }

        private void createCircle(){
            this.circlePaint = new Paint();
            this.circlePath = new Path();

            this.circlePaint.setDither(true);
            this.circlePaint.setAntiAlias(true);
            this.circlePaint.setColor(this.finalColor);
            this.circlePaint.setStyle(Paint.Style.STROKE);
            this.circlePaint.setStrokeWidth(this.finalWidthCircle);
        }

        private void createBrush(){
            this.brushPaint = new Paint();
            this.brushPath = new Path();

            this.brushPaint.setDither(true);
            this.brushPaint.setAntiAlias(true);
            this.brushPaint.setColor(this.finalColor);
            this.brushPaint.setStyle(Paint.Style.STROKE);
            this.brushPaint.setStrokeWidth(this.finalWidthBrush);
            this.brushPaint.setStrokeCap(Paint.Cap.ROUND);
        }

        private void createButtons(Context context){
            this.params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

            this.row = new LinearLayout(context);
            this.row.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);

            this.createEarser(context);
            this.createPencil(context);
            this.createClean(context);

            this.row.addView(this.eraser);
            this.row.addView(this.pencil);
            this.row.addView(this.clean);
        }

        private void createEarser(Context context){
            this.eraser = new Button(context);
            this.eraser.setText("Borrador");
            this.eraser.setWidth(200);

            this.eraser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setEraser();
                }
            });
        }

        private void createClean(Context context){
            this.clean = new Button(context);
            this.clean.setText("Limpiar");
            this.clean.setWidth(200);

            this.clean.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setClean();
                }
            });
        }

        private void createPencil(Context context){
            this.pencil = new Button(context);
            this.pencil.setText("Lapiz");
            this.pencil.setWidth(200);

            this.pencil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setPencil();
                }
            });
        }

        private void setEraser(){

            this.brushPaint.setStrokeWidth(this.finalWidthBrush + 20);
            this.brushPaint.setColor(Color.WHITE);
        }

        private void setPencil(){
            this.brushPaint.setStrokeWidth( this.finalWidthBrush );
            this.brushPaint.setColor(this.finalColor);
        }

        private void setClean(){
            this.brushPath.reset();
            this.circlePath.reset();
            this.canvas.drawColor(Color.WHITE);
            this.invalidate();
            this.setPencil();
        }
        private void drawCircle(Canvas canvas){
            Paint paint = new Paint();//nos ayuda a mantener el estilo, No es la figura geometrica

            paint.setDither(true);
            paint.setAntiAlias(true);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(this.finalColor);
            canvas.drawCircle(300, 200, 200, paint);
        }

        private void actionDown(float posx, float posy){
            this.lastPosX = posx;
            this.lastPosY = posy;

            this.circlePath.reset();
            this.brushPath.reset();
            this.brushPath.moveTo(lastPosX, lastPosY);
        }

        private void actionMove(float posx, float posy){
            this.circlePath.reset();

            this.brushPath.quadTo(this.lastPosX, this.lastPosY, (posx + this.lastPosX ) / 2,(posy + this.lastPosY ) / 2 );
            this.lastPosX = posx;
            this.lastPosY = posy;

            this.circlePath.addCircle(posx, posy, 30, Path.Direction.CCW);
        }

        private void actionUp(){
            this.circlePath.reset();
            this.canvas.drawPath(this.brushPath, this.brushPaint);
            this.brushPath.reset();
        }

        private void showMessage(String message){
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onSizeChanged(int w, int h, int wold, int hold){
            super.onSizeChanged(w,h,wold,hold);

            this.myBitMap = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
            this.canvas = new Canvas(this.myBitMap);
        }

        @Override
        protected void onDraw(Canvas canvas){
            super.onDraw(canvas);

            canvas.drawBitmap( this.myBitMap,0,0,this.bitMaptPaint);
            canvas.drawPath(this.brushPath, this.brushPaint);
            canvas.drawPath(this.circlePath, this.circlePaint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event){
            this.showMessage("Touch Event");

            float posx = event.getX();
            float posy = event.getY();

            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    this.actionDown(posx, posy);
                    break;
                case MotionEvent.ACTION_MOVE:
                    this.actionMove(posx, posy);
                    break;
                case MotionEvent.ACTION_UP:
                    this.actionUp();
                    break;
            }

            this.invalidate();

            return true;
        }

    }

}

package com.atasoft.flangeassist.fragments.cashcounter.counterobjects;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.atasoft.flangeassist.MainActivity;
import com.atasoft.flangeassist.fragments.cashcounter.CashCounter2;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by ataboo on 2016-05-14.
 */
public abstract class CounterScene {
    protected Bitmap backgroundTexture;
    protected Context context;
    protected float sceneRatio = 720f/1080f;
    protected Rect sceneRect = new Rect(0, 0, 720, 1024);
    protected EarningText earningText;
    protected TextureBox textureBox = MainActivity.TEXTURE_BOX;
    public Scene scene;

    public enum Scene{
        PULP_MILL("Pulp Mill"),
        OIL_DRIP("Oil Drip"),
        HYDRO_DAM("Hydro Dam");

        public final String name;
        Scene(String name){
            this.name = name;
        }

        public CounterScene makeScene(Context context, IntVector screenSize){
            switch (this){
                default: case PULP_MILL:
                    return new PulpMillScene(context, screenSize);
                case OIL_DRIP:
                    return new OilDripScene(context, screenSize);
                case HYDRO_DAM:
                    return new HydroScene(context, screenSize);
            }
        }

        public static String[] getNames(){
            ArrayList<String> nameList = new ArrayList<>();

            for(Scene scene: Scene.values()){
                nameList.add(scene.name);
            }

            return nameList.toArray(new String[nameList.size()]);
        }

        public static Scene getSceneFromName(String name){
            for(Scene scene: Scene.values()){
                if(scene.name.equals(name)){
                    return scene;
                }
            }
            return null;
        }
    }

    protected ArrayList<CounterAnim> animations = new ArrayList<>();
    protected ArrayList<CounterAnim> graveYard = new ArrayList<>();

    public void addFineAnim(long startTime, float earnings, CashCounter2.EarningType earningType){
        earningText.animateEarnings(earnings, startTime, earningType);
    }

    public abstract void addCoarseAnim(long startTime, float earnings);

    public void screenResize(IntVector size) {
        this.sceneRect = sceneSize(size);

        for(CounterAnim anim: animations){
            anim.resize(sceneRect);
        }
    }

    public void update(){
        for(CounterAnim anim: graveYard){
            anim.dispose();
            animations.remove(anim);
        }
        graveYard.clear();
    }

    public void draw(long time, Canvas canvas, Paint paint) {
        for(CounterAnim anim: animations){
            anim.draw(time, canvas, paint);

            if(anim instanceof OnceAnimation){
                if(((OnceAnimation) anim).isDone){
                    graveYard.add(anim);
                }
            }
        }
    }

    public void setEarnings(float earnings, CashCounter2.EarningType earningType){
        earningText.setEarnings(earnings, earningType);
    }

    public void dispose() {
        for (CounterAnim anim : animations) {
            anim.dispose();
        }

        backgroundTexture.recycle();
    }

    protected Rect sceneSize(IntVector screenSize){
        int width = screenSize.x;
        int height = screenSize.y;

        if(screenSize.y > screenSize.x){
            height = (int) (sceneRatio * width);
            int vertOffset = (screenSize.y - height) / 2;

            return new Rect(0, vertOffset, width, height + vertOffset);
        } else {
            width = (int) (height / sceneRatio);
            int horizOffset = (screenSize.x - width) / 2;

            return new Rect(horizOffset, 0, width + horizOffset, height);
        }
    }

    public static void disposeTextures(Bitmap[] bitmaps){
        if(bitmaps == null){
            return;
        }

        for(Bitmap bitmap: bitmaps){
            bitmap.recycle();
        }
    }

    protected static Bitmap[] getBitmapsFromSeries(Context context, String dirPath, String rootName) {
        AssetManager assetManager = context.getAssets();

        ArrayList<Bitmap> bitmapList = new ArrayList<>();

        try {
            String[] files = assetManager.list(dirPath);
            for (String file: files) {
                if(!file.contains(rootName)){
                    Log.w("AtaSprite", String.format("File: %s in %s does not contain rootName: %s", file, dirPath, rootName));
                    continue;
                }

                Log.w("AtaSprite", "Opening: " + file);

                Bitmap texture = getBitmapFromAssets(assetManager, dirPath + "/" + file);

                if(texture != null){
                    bitmapList.add(texture);
                } else {
                    Log.e("AtaSprite", "Returned null when attempting to open " + dirPath + file);
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        return bitmapList.toArray(new Bitmap[bitmapList.size()]);
    }

    protected static Bitmap getBitmapFromAssets(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        Bitmap bitmap = null;
        try{
            bitmap = getBitmapFromAssets(assetManager, filePath);
        } catch (IOException e){
            e.printStackTrace();
        }

        return bitmap;
    }

    protected static Bitmap getBitmapFromAssets(AssetManager assetManager, String filePath) throws IOException{
        InputStream istr;
        Bitmap bitmap = null;

        istr = assetManager.open(filePath);
        bitmap = BitmapFactory.decodeStream(istr);
        istr.close();

        return bitmap;
    }

    protected int getCents(float earnings){
        float difference = (earnings - earningText.getEarnings());
        return Math.round(difference * 100);
    }
}

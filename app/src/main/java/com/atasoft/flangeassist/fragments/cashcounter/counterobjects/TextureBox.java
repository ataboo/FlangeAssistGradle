package com.atasoft.flangeassist.fragments.cashcounter.counterobjects;

import android.graphics.Bitmap;

import com.atasoft.flangeassist.fragments.cashcounter.scenes.CounterScene;

import java.util.HashMap;

/**
 * Created by ataboo on 2016-05-15.
 */
public class TextureBox {
    private HashMap<String, Bitmap[]> textureMap = new HashMap<>();

    public void dispose(){
        for(Bitmap[] textureSet: textureMap.values()){
            CounterScene.disposeTextures(textureSet);
        }

        textureMap.clear();
    }

    public void addTextureSeries(String name, Bitmap[] textures){
        textureMap.put(name, textures);
    }

    public Bitmap[] getTextureSet(String name){
        return textureMap.get(name);
    }

    public boolean isEmpty(){
        return textureMap.size() == 0;
    }

    public boolean contains(String key){
        return textureMap.containsKey(key);
    }
}

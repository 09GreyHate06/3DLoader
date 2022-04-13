package utils;

import glCore.renderer.Shader;
import glCore.renderer.Texture2D;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class AssetManager {

    private static HashMap<String, Shader> _shaderMap = new HashMap<>();
    private static HashMap<String, Texture2D> _textureMap = new HashMap<>();

    public static void destroyAssets(){
        for(var shader : _shaderMap.values())
            shader.destroy();

        for(var tex : _textureMap.values())
            tex.destroy();

        _textureMap.clear();
        _shaderMap.clear();
    }

    public static Shader loadAndAddShader(String key, String filename){
        var shader = new Shader(filename);
        var prevShader = _shaderMap.put(key, shader);
        if(prevShader != null)
            prevShader.destroy();
        return shader;
    }

    public static Shader loadAndAddShader(String key, String vertSrc, String fragSrc){
        var shader = new Shader(vertSrc, fragSrc);
        var prevShader = _shaderMap.put(key, shader);
        if(prevShader != null)
            prevShader.destroy();
        return shader;
    }

    public static Shader addShader(String key, Shader shader){
        var prevShader = _shaderMap.put(key, shader);
        if(prevShader != null)
            prevShader.destroy();
        return shader;
    }

    public static Shader getShader(String key){
        return _shaderMap.get(key);
    }

    public static boolean removeShader(String key){
        var shader = _shaderMap.get(key);
        if(shader != null){
            shader.destroy();
            _shaderMap.remove(key);
            return true;
        }
        return false;
    }

    public static Texture2D loadAndAddTexture2D(String key, String filename, boolean flipImageY, int minFilter, int magFilter, int wrapS, int wrapT){
        var texture = new Texture2D(filename, flipImageY, minFilter, magFilter, wrapS, wrapT);
        var prevTexture = _textureMap.put(key, texture);
        if(prevTexture != null)
            prevTexture.destroy();
        return texture;
    }

    public static Texture2D loadAndAddTexture2D(String key, ByteBuffer pixels, int width, int height, int nrComp,
                                                int minFilter, int magFilter, int wrapS, int wrapT){
        var texture = new Texture2D(pixels, width, height, nrComp, minFilter, magFilter, wrapS, wrapT);
        var prevTexture = _textureMap.put(key, texture);
        if(prevTexture != null)
            prevTexture.destroy();
        return texture;
    }

    public static Texture2D addTexture2D(String key, Texture2D texture){
        var prevTexture = _textureMap.put(key, texture);
        if(prevTexture != null)
            prevTexture.destroy();
        return texture;
    }

    public static Texture2D getTexture2D(String key){
        return _textureMap.get(key);
    }

    public static boolean removeTexture2D(String key){
        var texture = _textureMap.get(key);
        if(texture != null){
            texture.destroy();
            _textureMap.remove(key);
            return true;
        }
        return false;
    }
}

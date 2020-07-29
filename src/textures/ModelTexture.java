package textures;

public class ModelTexture {

    private int textureID;
    private float shineDamper = 1;
    private float reflectivity = 0;
    private boolean hadTransparency = false;
    private boolean useFakeLightning = false;

    public ModelTexture(int id){
        this.textureID = id;
    }

    public int getTextureID() {
        return textureID;
    }

    public float getShineDamper() {
        return shineDamper;
    }

    public void setShineDamper(float shineDamper) {
        this.shineDamper = shineDamper;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public void setReflectivity(float reflectivity) {
        this.reflectivity = reflectivity;
    }

    public boolean isHadTransparency() {
        return hadTransparency;
    }

    public void setHadTransparency(boolean hadTransparency) {
        this.hadTransparency = hadTransparency;
    }

    public boolean isUseFakeLightning() {
        return useFakeLightning;
    }

    public void setUseFakeLightning(boolean useFakeLightning) {
        this.useFakeLightning = useFakeLightning;
    }
}

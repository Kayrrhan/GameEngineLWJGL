package textures;

public class ModelTexture {

    private int textureID;
    private int normalMap;
    private int specularMap;
    private float shineDamper = 1;
    private float reflectivity = 0;
    private boolean hadTransparency = false;
    private boolean useFakeLightning = false;
    private boolean hadSpecularMap = false;
    private int numberOfRows = 1;

    public ModelTexture(int id){
        this.textureID = id;
    }

    public int getTextureID() {
        return textureID;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public float getShineDamper() {
        return shineDamper;
    }

    public void setShineDamper(float shineDamper) {
        this.shineDamper = shineDamper;
    }

    public void setSpecularMap(int specularMap){
        this.specularMap = specularMap;
        hadSpecularMap = true;
    }

    public boolean hasSpecularMap(){
        return hadSpecularMap;
    }

    public int getSpecularMap(){
        return specularMap;
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

    public boolean isHasTransparency() {
        return hadTransparency;
    }

    public int getNormalMap() {
        return normalMap;
    }

    public void setNormalMap(int normalMap) {
        this.normalMap = normalMap;
    }

    public int getID() {
        return textureID;
    }
}

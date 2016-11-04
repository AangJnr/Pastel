package ind.aang.pastel;

/**
 * Created by AangJnr on 10/5/16.
 */
public class ColorItem {

    //public String colorView;
    public String colorCode;


    public ColorItem(){
        super();
    }

    public ColorItem(String color_code){
        this.colorCode = color_code;
    }

    public String getColorCode(){
        return colorCode;
    }

    public void setColorCode(String colorCode){
        this.colorCode = colorCode;

    }
}

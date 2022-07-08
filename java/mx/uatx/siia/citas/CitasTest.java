package mx.uatx.siia.citas;

import com.sun.istack.NotNull;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "test")
@ViewScoped
public class CitasTest implements Serializable {
    public CitasHelper citasHelper;
    private boolean hasDataAreas = false;
    private boolean hasDataTramites = false;
    @NotNull
    private String valueComboAreas = null;
    @NotNull
    private String valueComboTramites = null;
    private List<SelectItem> listaAreas;
    private List<SelectItem> listaTramites;

    /*-----------CONSTRUCTOR */
    public CitasTest(){
        citasHelper = new CitasHelper();
    }


    public List<SelectItem> obtenerAreas(){
        System.out.println("---- RETURN DATA OF AREAS");
        return listaAreas;
    }

    public void updateForm(){
        setListaAreas(citasHelper.getSelectAreas());
        hasDataAreas = true;
    }

    public List<SelectItem> obtenerTramites(){
        System.out.println("-------SOY EL COMBO DE LOS TRAMITES POR AREA---\n");
        return listaTramites;
    }

    public void showTramite(){
        System.out.println("[VALUE] de Areas => "+getValueComboAreas());
        System.out.println("show tramites");
        setListaTramites(citasHelper.getSelectTramites(getValueComboAreas()));
        hasDataTramites = true;
    }

    public void showNext(){
        System.out.println("[VALUE] de Tramites => "+getValueComboTramites());
    }

    /*----- GETTERS AND SETTERS  ----*/

    public String getValueComboAreas() {return valueComboAreas;}

    public void setValueComboAreas(String valueComboAreas) {this.valueComboAreas = valueComboAreas;}
    public List<SelectItem> getListaAreas() {return listaAreas;}
    public void setListaAreas(List<SelectItem> listaAreas) { this.listaAreas = listaAreas;}
    public boolean isHasDataAreas() {
        return hasDataAreas;
    }
    public void setHasDataAreas(boolean hasDataAreas) {
        this.hasDataAreas = hasDataAreas;
    }
    public CitasHelper getCitasHelper() {
        return citasHelper;
    }
    public void setCitasHelper(CitasHelper citasHelper) {
        this.citasHelper = citasHelper;
    }
    public boolean isHasDataTramites() {return hasDataTramites;}
    public void setHasDataTramites(boolean hasDataTramites) {this.hasDataTramites = hasDataTramites;}
    public String getValueComboTramites() {return valueComboTramites;}
    public void setValueComboTramites(String valueComboTramites) {this.valueComboTramites = valueComboTramites;}

    public List<SelectItem> getListaTramites() {return listaTramites;}

    public void setListaTramites(List<SelectItem> listaTramites) {this.listaTramites = listaTramites;}
}

package mx.uatx.siia.citas.controller;

import mx.uatx.siia.citas.Tramites.business.TramitesBusiness;
import mx.uatx.siia.citas.areas.business.AreasBusiness;
import mx.uatx.siia.comun.helper.VistasHelper;
import mx.uatx.siia.serviciosUniversitarios.dto.ResultadoTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;

@SessionScoped
@ManagedBean(name = "adminlogin")
public class AdminLoginBean implements Serializable {


    private static final long serialVersionUID = -8231460910050633212L;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @ManagedProperty("#{msj}")
    private ResourceBundle msj;

    @ManagedProperty("#{areasBusiness}")
    private AreasBusiness areasBusiness;

    private final VistasHelper vHelp = new VistasHelper();
    private List<SelectItem> listAreas;
    private String strUsuario;
    private String strCurrentAreaId;

    private String strCurrentLabelArea;

    public List<SelectItem> getListAreas(){
        ResultadoTO resultado  = areasBusiness.obtenerAreas();
        listAreas = (List<SelectItem>) resultado.getObjeto();
        return listAreas;
    }

    public void listerpostReporte(){

        for(SelectItem item : listAreas){
            if (item.getValue().toString().equals(strCurrentAreaId)){
                strCurrentLabelArea = item.getLabel();
            }
        }
        logger.info("label area -> "+strCurrentLabelArea);
    }

    public void login(){

        vHelp.redireccionar("/vistas/administracionCitas/index.uat");

    }

    /*   GETTERS AND SETTERS   */

    public ResourceBundle getMsj() {
        return msj;
    }

    public void setMsj(ResourceBundle msj) {
        this.msj = msj;
    }

    public void setListAreas(List<SelectItem> listAreas) {
        this.listAreas = listAreas;
    }

    public AreasBusiness getAreasBusiness() {
        return areasBusiness;
    }

    public String getStrUsuario() {
        return strUsuario;
    }

    public void setStrUsuario(String strUsuario) {
        this.strUsuario = strUsuario;
    }

    public String getStrCurrentLabelArea() {
        return strCurrentLabelArea;
    }

    public void setStrCurrentLabelArea(String strCurrentLabelArea) {
        this.strCurrentLabelArea = strCurrentLabelArea;
    }

    public String getStrCurrentAreaId() {
        return strCurrentAreaId;
    }

    public void setStrCurrentAreaId(String strCurrentAreaId) {
        this.strCurrentAreaId = strCurrentAreaId;
    }

    public void setAreasBusiness(AreasBusiness areasBusiness) {
        this.areasBusiness = areasBusiness;
    }
}

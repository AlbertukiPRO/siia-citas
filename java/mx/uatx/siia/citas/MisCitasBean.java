package mx.uatx.siia.citas;

import mx.uatx.siia.citas.modelo.MisCitas;
import mx.uatx.siia.citas.modelo.citasBusiness.CitaBusiness;
import mx.uatx.siia.citas.modelo.enums.URLs;
import mx.uatx.siia.comun.helper.VistasHelper;
import mx.uatx.siia.serviciosUniversitarios.dto.ResultadoTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;

@ManagedBean(name = "misictas")
@ViewScoped
public class MisCitasBean implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2621529818431646329L;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @ManagedProperty("#{msj}")
    private ResourceBundle msj;

    @ManagedProperty("#{citaBusiness}")
    private CitaBusiness citaBusiness;

    public CitaBusiness getCitaBusiness() {
        return citaBusiness;
    }

    public void setCitaBusiness(CitaBusiness citaBusiness) {
        this.citaBusiness = citaBusiness;
    }

    private final VistasHelper vHelp = new VistasHelper();

    /*LOCAL ATTRIBUTES*/

    public Logger getLogger() {
        return logger;
    }

    public String getLocalArea() {
        return localArea;
    }

    public void setLocalArea(String localArea) {
        this.localArea = localArea;
    }

    public String getLocalTramite() {
        return localTramite;
    }

    public void setLocalTramite(String localTramite) {
        this.localTramite = localTramite;
    }

    private String idUser;
    private boolean renderMisCitas = false;
    private String strLocalMatricula;
    private String strLocalNombre;


    private List<MisCitas> listMisCitas;
    private String localArea;
    private String localTramite;
    private String motivoCancelacion;

    private boolean btnLookForCitas = false;

    public void UpdateValores(String nombre, String matricula){
        strLocalMatricula = matricula;
        strLocalNombre = nombre;
        GetCitas();
    }

    public void GetCitas(){
        ResultadoTO resultado = citaBusiness.miCita(URLs.MiCita.getValor(), strLocalMatricula);
        listMisCitas = (List<MisCitas>) resultado.getObjeto();
        renderMisCitas = true;
    }

    public boolean isCancelable(String estatus){
        if (estatus.equals("Agendada")){
            return true;
        }else{
            return false;
        }
    }

    public void SetModalCancelar(String area, String tramite){
        localArea =  area;
        localTramite = tramite;
        /**
         * TODO implementacion para borrar la cita
         */
    }

    public void Cancelar(){
        System.out.println("--- CANCELAR CITA--");
    }

    public ResourceBundle getMsj() {
        return msj;
    }

    public boolean isBtnLookForCitas() {
        return btnLookForCitas;
    }

    public void setBtnLookForCitas(boolean btnLookForCitas) {
        this.btnLookForCitas = btnLookForCitas;
    }

    public void setMsj(ResourceBundle msj) {
        this.msj = msj;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public boolean isRenderMisCitas() {
        return renderMisCitas;
    }

    public void setRenderMisCitas(boolean renderMisCitas) {
        this.renderMisCitas = renderMisCitas;
    }

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public void setMotivoCancelacion(String motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }

    public String getStrLocalMatricula() {
        return strLocalMatricula;
    }

    public void setStrLocalMatricula(String strLocalMatricula) {
        this.strLocalMatricula = strLocalMatricula;
    }

    public String getStrLocalNombre() {
        return strLocalNombre;
    }

    public void setStrLocalNombre(String strLocalNombre) {
        this.strLocalNombre = strLocalNombre;
    }

    public List<MisCitas> getListMisCitas() {
        return listMisCitas;
    }

    public void setListMisCitas(List<MisCitas> listMisCitas) {
        this.listMisCitas = listMisCitas;
    }

}

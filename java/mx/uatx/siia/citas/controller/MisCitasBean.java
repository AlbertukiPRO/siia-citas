package mx.uatx.siia.citas.controller;

import mx.uatx.siia.citas.modelo.MisCitas;
import mx.uatx.siia.citas.modelo.SIMSCITAS;
import mx.uatx.siia.citas.modelo.citasBusiness.CitaBusiness;
import mx.uatx.siia.citas.modelo.citasBusiness.MethodsGenerics;
import mx.uatx.siia.citas.modelo.enums.URLs;
import mx.uatx.siia.comun.helper.VistasHelper;
import mx.uatx.siia.serviciosUniversitarios.dto.ResultadoTO;
import mx.uatx.siia.serviciosUniversitarios.enums.SeveridadMensajeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@ManagedBean(name = "misictas")
@RequestScoped
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
    private String stridCita;
    private String strMotivo;
    private boolean wasCanceled;
    private List<MisCitas> listMisCitas;
    private String localArea;
    private String localTramite;
    private String motivoCancelacion;
    private boolean btnLookForCitas = false;
    private Long idHistorico;

    public void UpdateValores(String nombre, String matricula) {
        strLocalMatricula = matricula;
        strLocalNombre = nombre;
        idHistorico = 30643L;
        GetCitas();
    }

    public void GetCitas() {
//        ResultadoTO resultado = citaBusiness.miCita(URLs.MiCita.getValor(), strLocalMatricula);
        ResultadoTO resultado = citaBusiness.obtenerMisCitas(idHistorico);
        listMisCitas = (List<MisCitas>) resultado.getObjeto();
        renderMisCitas = true;
    }

    public boolean isCancelable(String estatus){
        return estatus.equals("Agendada");
    }

    public void SetModalCancelar(String tramite){
        System.out.println("----- SET DATA FROM CITA ----->"+tramite);

        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
        String data = params.get("idcita");

        logger.info("----> Coming data from idParam -> "+data);
    }

    public void Cancelar(){
        System.out.println("--- CANCELAR CITA--");
        ResultadoTO resultado = null;
        try {
            resultado = citaBusiness.cancelarCita(stridCita, URLEncoder.encode(motivoCancelacion, String.valueOf(StandardCharsets.UTF_8)));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        int nRows = (int) resultado.getObjeto();
        if (nRows!=0){
            wasCanceled = !wasCanceled;
            vHelp.redireccionar("/vistas/test/index.uat");
        }else{
            FacesContext.getCurrentInstance().
                    addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR:", "No se pudo cancelar su cita intentelo mas tarde."));
        }
    }

    public MisCitasBean(){
        if (wasCanceled){
            FacesContext.getCurrentInstance().
                    addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "INF:", "Su cita fue cancelada exitosamente"));
        }
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

    public void hideMisCitas(){
        renderMisCitas = false;
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

    public String getStridCita() {
        return stridCita;
    }

    public void setStridCita(String stridCita) {
        this.stridCita = stridCita;
    }

    public String getStrMotivo() {
        return strMotivo;
    }

    public void setStrMotivo(String strMotivo) {
        this.strMotivo = strMotivo;
    }
}

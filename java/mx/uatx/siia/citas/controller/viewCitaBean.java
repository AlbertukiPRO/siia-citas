package mx.uatx.siia.citas.controller;

import mx.uatx.siia.citas.SIMSCITAS;
import mx.uatx.siia.citas.areas.business.AreasBusiness;
import mx.uatx.siia.citas.citasBusiness.CitaBusiness;
import mx.uatx.siia.citas.citasBusiness.ObjectMapperUtils;
import mx.uatx.siia.citas.enums.EstatusCitas;
import mx.uatx.siia.comun.helper.VistasHelper;
import mx.uatx.siia.serviciosUniversitarios.dto.CitasTO;
import mx.uatx.siia.serviciosUniversitarios.dto.ResultadoTO;
import mx.uatx.siia.serviciosUniversitarios.enums.SeveridadMensajeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ResourceBundle;

@RequestScoped
@ManagedBean(name = "viewcita")
public class viewCitaBean implements Serializable {


    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 5531583753441326730L;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @ManagedProperty("#{msj}")
    private ResourceBundle msj;

    @ManagedProperty("#{areasBusiness}")
    private AreasBusiness areasBusiness;

    @ManagedProperty("#{citaBusiness}")
    private CitaBusiness citaBusiness;

    private final VistasHelper vHelp = new VistasHelper();


    @ManagedProperty(value = "#{param.idarea}")
    private String idarea;
    @ManagedProperty(value = "#{param.idcita}")
    private String id;
    @ManagedProperty(value = "#{param.matricula}")
    private String matricula;
    private CitasTO modelCita;
    private String retroalimentacion;

    @PostConstruct
    public void init(){
        String[] params = new String[]{idarea,matricula,id};
//        ResultadoTO resultado = areasBusiness.obtenerCita(2, URLs.MiCita.getValor(), params);
        ResultadoTO resultado = areasBusiness.obtenerCita(Long.parseLong(matricula), Long.parseLong(id));
        modelCita = ObjectMapperUtils.map((SIMSCITAS) resultado.getObjeto(), CitasTO.class);

        retroalimentacion = modelCita.getStrRetroalimentacion();
    }

    public void regresar(){
        vHelp.redireccionar("/vistas/administracionCitas/index.uat");
    }

    public void sendRetro(){
         ExternalContext data =  FacesContext.getCurrentInstance().getExternalContext();
         data.getFlash().put("idarea", idarea);
         data.getFlash().put("idcita", id);
         data.getFlash().put("matricula", matricula);

        ResultadoTO resultado = citaBusiness.guardarRetro(modelCita.getIntIdCita(), retroalimentacion);
        if (resultado.isBlnValido()) {
            resultado.agregarMensaje(SeveridadMensajeEnum.INFO, "comun.msg.citas.retro.save.ok");
            vHelp.pintarMensajes( msj,resultado);
        }else{
            resultado.agregarMensaje(SeveridadMensajeEnum.ERROR, "comun.msg.citas.retro.save.error");
            vHelp.pintarMensajes( msj,resultado);
        }
    }

    public void citaCompletada(){
        ResultadoTO resultado = citaBusiness.cambiarEstatus(modelCita.getIntIdCita(), EstatusCitas.CitaCompleta.getValor());
        if (resultado.isBlnValido()){
            resultado.agregarMensaje(SeveridadMensajeEnum.INFO, "comun.msj.citass.estatus.change.exito");
            vHelp.pintarMensajes(msj, resultado);
        }else {
            resultado.agregarMensaje(SeveridadMensajeEnum.ERROR, "comun.msj.citass.estatus.change.someerror");
            vHelp.pintarMensajes(msj, resultado);
        }
    }

    public void citaCancelar(){
        ResultadoTO resultado = citaBusiness.cambiarEstatus(modelCita.getIntIdCita(), EstatusCitas.CitaCancelada.getValor());
        if (resultado.isBlnValido()){
            resultado.agregarMensaje(SeveridadMensajeEnum.INFO, "comun.msj.citass.estatus.change.exito");
            vHelp.pintarMensajes(msj, resultado);
        }else {
            resultado.agregarMensaje(SeveridadMensajeEnum.ERROR, "comun.msj.citass.estatus.change.someerror");
            vHelp.pintarMensajes(msj, resultado);
        }
    }

    public AreasBusiness getAreasBusiness() {
        return areasBusiness;
    }

    public void setAreasBusiness(AreasBusiness areasBusiness) {
        this.areasBusiness = areasBusiness;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdarea() {
        return idarea;
    }

    public void setIdarea(String idarea) {
        this.idarea = idarea;
    }

    public CitaBusiness getCitaBusiness() {
        return citaBusiness;
    }

    public void setCitaBusiness(CitaBusiness citaBusiness) {
        this.citaBusiness = citaBusiness;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public CitasTO getModelCita() {
        return modelCita;
    }

    public String getRetroalimentacion() {
        return retroalimentacion;
    }

    public void setRetroalimentacion(String retroalimentacion) {
        this.retroalimentacion = retroalimentacion;
    }

    public void setModelCita(CitasTO modelCita) {
        this.modelCita = modelCita;
    }

    public ResourceBundle getMsj() {
        return msj;
    }

    public void setMsj(ResourceBundle msj) {
        this.msj = msj;
    }
}

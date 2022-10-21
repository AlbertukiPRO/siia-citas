package mx.uatx.siia.citas.controller;

import mx.uatx.siia.citas.MisCitas;
import mx.uatx.siia.citas.SIMSCITAS;
import mx.uatx.siia.citas.areas.business.AreasBusiness;
import mx.uatx.siia.citas.citasBusiness.ObjectMapperUtils;
import mx.uatx.siia.citas.enums.URLs;
import mx.uatx.siia.comun.helper.VistasHelper;
import mx.uatx.siia.serviciosUniversitarios.dto.CitasTO;
import mx.uatx.siia.serviciosUniversitarios.dto.ResultadoTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
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

    private final VistasHelper vHelp = new VistasHelper();


    @ManagedProperty(value = "#{param.idarea}")
    private String idarea;
    @ManagedProperty(value = "#{param.idcita}")
    private String id;
    @ManagedProperty(value = "#{param.matricula}")
    private String matricula;
    private CitasTO modelCita;

    @PostConstruct
    public void init(){
        String[] params = new String[]{idarea,matricula,id};
//        ResultadoTO resultado = areasBusiness.obtenerCita(2, URLs.MiCita.getValor(), params);
        ResultadoTO resultado = areasBusiness.obtenerCita(Long.parseLong(matricula), Long.parseLong(id));
        modelCita = ObjectMapperUtils.map((SIMSCITAS) resultado.getObjeto(), CitasTO.class);
        logger.info(modelCita.toString());
    }

    public void regresar(){
        vHelp.redireccionar("/vistas/administracionCitas/index.uat");
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

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public CitasTO getModelCita() {
        return modelCita;
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

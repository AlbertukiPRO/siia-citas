package mx.uatx.siia.citas.controller;

import com.sun.istack.NotNull;
import mx.uatx.siia.citas.Tramites.business.TramitesBusiness;
import mx.uatx.siia.citas.areas.business.AreasBusiness;
import mx.uatx.siia.citas.areas.business.SiPaAreasConfiguraciones;
import mx.uatx.siia.citas.citasBusiness.CitaBusiness;
import mx.uatx.siia.citas.citasBusiness.MethodsGenerics;
import mx.uatx.siia.citas.citasBusiness.ObjectMapperUtils;
import mx.uatx.siia.citas.dto.ConfiguacionesDTO;
import mx.uatx.siia.comun.helper.VistasHelper;
import mx.uatx.siia.reportes.FieldsNuevaCita;
import mx.uatx.siia.serviciosUniversitarios.dto.ResultadoTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;

/**
 * @author Alberto Noche Rosas
 * @date 22/09/2022
 * @apiNote Bean para el manejo del frontend del módulo citas.
 */
@ViewScoped
@ManagedBean(name = "nuevacita")
public class NuevaCitaBean implements Serializable {
    /**
     * SerialVersion
     */
    private static final long serialVersionUID = 2205319003472956990L;
    protected final Logger logger = LoggerFactory.getLogger(getClass());


    /**
     * @LocalAtributtes
     */
        private List<SelectItem> listAreas;
        private List<SelectItem> listTramites;
        private List<String> ListHorariosShow;
        private List<String> listProgramaEdu;
        private List<String> listDatosAlumno;
        public ConfiguacionesDTO listaConfig;

        private boolean hasDataTramites = false;
        private boolean hasHorarios = false;
        private boolean isCitaAgendada = false;
        private boolean hasCalendar = false;
        private boolean isForHidden = false;
        private boolean btnLookNewCita = false;
        public boolean flagHorarioReservado = false;
        private boolean hasDataAreas = false;
        private boolean showDataEstudiante = false;
        private boolean showlinktramitereq = false;
        private boolean showlinktext = false;

        @NotNull
        private String strFechasDisableCalendar = null;
        @NotNull
        private String strCurrentArea = null;
        @NotNull
        private String strCurrentTramite = null;
        @NotNull
        private String strCurrentCalendar = null;
        @NotNull
        private String strCurrentHora = null;
        @NotNull
        private String strMotivoCita = null;

        private String strLocalNombreUser;
        private String strLocalMatricula;
        private String strLocalArea;
        private String strLocalTramite;
        private String strLocalFecha;
        private String strLocalHorario;
        private String strLocalDescripcion;
        private String strProgramaEdu;
        private String tomaxDate;
        private String tominDate;
        private String strRequisitos;

    /**
     * @BusinessProperties
     */
        @ManagedProperty("#{areasBusiness}")
        private AreasBusiness areasBusiness;
        @ManagedProperty("#{tramitesBusiness}")
        private TramitesBusiness tramitesBusiness;
        @ManagedProperty("#{citaBusiness}")
        private CitaBusiness citaBusiness;
        @ManagedProperty("#{msj}")
        private ResourceBundle msj;
        final private VistasHelper vHelp = new VistasHelper();


    /**
     * @Listeners que obtiene los trámites segun el área seleccionada además se inicializa las configuraciones del área
     */
    public void listenerPostAreas(){
        ResultadoTO res = tramitesBusiness.obtenerTramites(Integer.parseInt(strCurrentArea));
        listTramites = (List<SelectItem>) res.getObjeto();
        listAreas.forEach(selectItem -> {
            if ( selectItem.getValue().toString().equals(strCurrentArea))
                strLocalArea = selectItem.getLabel();
        });

        hasDataTramites = res.isBlnValido();

        inicializarSettings();
    }

    public void listenerPostProgromaEdu(){
        listDatosAlumno = Arrays.asList("Yair Ivan Valencia Perez","20082306","Facultad de Ciencias Básicas Ingeniería y Tecnología campus Apizaco", "Ingeniería En Computación", "9","B");
        showDataEstudiante = true;
        hasDataAreas = true;
        strLocalMatricula = listDatosAlumno.get(1);
        strLocalNombreUser = listDatosAlumno.get(0);
    }

    /**
     * @Listeners que obtienes los horarios del área desde la DB
     */
    public void listenerPostTramites() {

        for (SelectItem list : listTramites) {
            if (list.getValue().equals(Integer.parseInt(strCurrentTramite))) {
                strLocalTramite=list.getLabel();
            }
        }

        ResultadoTO resultadoF = areasBusiness.obtenerDiasInhabiles(strCurrentArea);
        /* Se crea el string con las fechas desactivadas.*/
        strFechasDisableCalendar = (
                MethodsGenerics.formattingStringFechasCalendar(
                        (List<String>) resultadoF.getObjeto()
                )
        );

        // Fechas minimas y maximas para renderizar en el calendario.
        tomaxDate = MethodsGenerics.lessOneDay((long) 60, false);
        tominDate = MethodsGenerics.lessOneDay((long) 30, true);

        requisitosTramite();

        hasHorarios = true;
        hasCalendar = true;
        strCurrentCalendar= null;
    }

    /**
     * @apiNote Metodo para obtener las configuraciones del área y mantenerlas en una Lista.
     */
    public void inicializarSettings(){
        ResultadoTO resultado = areasBusiness.obtenerConfiguracionArea(strCurrentArea);
        SiPaAreasConfiguraciones listfromdb = (SiPaAreasConfiguraciones) resultado.getObjeto();

        listaConfig = ObjectMapperUtils.map(listfromdb, ConfiguacionesDTO.class);
    }

    public void generarPDF(String idhistorical){
        System.out.println("--- GENERANDO PDF ---");
        ResultadoTO resultado = citaBusiness.obtenerIDfromCita(Long.parseLong(idhistorical));
        if (resultado.isBlnValido()){
            Integer foliocita = (Integer) resultado.getObjeto();
            final String datePrint = MethodsGenerics.getCurrentDate();

            ArrayList<FieldsNuevaCita> datos = new ArrayList<>();
            datos.add(new FieldsNuevaCita(
                    strLocalNombreUser,
                    MethodsGenerics.formatDate(strLocalFecha),
                    strLocalTramite,
                    listDatosAlumno.get(3),
                    strLocalMatricula,
                    strCurrentHora,
                    strMotivoCita,
                    "CI"+foliocita,
                    strRequisitos,
                    datePrint
            ));

            String sourFileName = "Comprobante";
            String rutaFiles = "resources/reportes/citas";
            HashMap<String, Object> parameters = new HashMap<>();

            parameters.put("nombreuser", "nombreuser");
            parameters.put("fechacita", "fechacita");
            parameters.put("tramite", "tramite");
            parameters.put("programaedu", "programaedu");
            parameters.put("matricula","matricula");
            parameters.put("hora", "hora");
            parameters.put("descripcion", "descripcion");
            parameters.put("foliocita", "foliocita");
            parameters.put("link", "link");
            parameters.put("fechaprint", "fechaprint");
            parameters.put("qrvalue", strLocalNombreUser+","+strLocalMatricula);
            vHelp.llenarYObtenerBytesReporteJasperPDF(rutaFiles, sourFileName, datos, parameters);

        }else{
            mostrarNotification(FacesMessage.SEVERITY_WARN, "ERROR:", "Ups! no pudimos obtener los datos intentalo mas tarde.");
        }

    }

    /**
     * @return List => SelectItem para el elemento SelectOneMenu de Bootfaces
     */
    public List<SelectItem> returnAreasList(){
        if (listAreas == null){
            ResultadoTO resultado = areasBusiness.obtenerAreas();
            listAreas = (List<SelectItem>) resultado.getObjeto();
        }
        return listAreas;
    }

    /**
     * @return List => SelectItem para el elemento SelectOneMenu de Bootfaces
     */
    public List<SelectItem> returnTramitesList() {
        return listTramites;
    }

    /**
     * @return {List} => Strings con los horarios finales y disponibles.
     */
    public List<String> returnHorariosList(){
        return ListHorariosShow;
    }


    public List<SelectItem> returnProgramaEduList(){
        return Collections.singletonList(new SelectItem("20181837", "[20181837] LICENCIATURA EN INGENIERÍA EN COMPUTACIÓN CAMPUS APIZACO (2018)"));
    }

    /**
     * @apiNote  Function que comparar los horarios reservados con el horario solicitado por usuario;
     */
    public void ComprobarHorario(){
        logger.info("Comprobar Horario para la fecha => [ "+ MethodsGenerics.formatDate(strCurrentCalendar) +" ]");
        String[] params = new String[]{strCurrentArea, MethodsGenerics.formatDate(strCurrentCalendar)};
        ResultadoTO resultadoH = areasBusiness.obtenerHorarios(params);
        logger.info("--RESULTADO-- \t"+resultadoH.getObjeto());
        logger.info("-- CONFIG: \t"+listaConfig.toString());
        List<String> horas = (List<String>) resultadoH.getObjeto();

        // * Los datos son strings asi que tenemos que parciarlos a int además de darle un formato correcto para el algoritmo de generacion de horarios.
        // * Ejemplo: "9:00"->.replace() |=> "9000".Integer.parse / 1000 | => 9 ()-> <Integer>
        List<String> listHorarios =
                MethodsGenerics.generarHorarios(
                Integer.parseInt(listaConfig.getHoraServicioInicio().replace(':','0'))/1000,
                Integer.parseInt(listaConfig.getHoraServicioFin().replace(':','0'))/1000,
                Integer.parseInt(listaConfig.getDuracionCitas()), horas);
        // El algoritmo generar horarios recibe tres parametros:
        // 1. Hora Incio => <int>
        // 2. Hora Fin => <int>
        // 3. DuracionCita => <int>
        // @return => List<String>

        logger.info("|----- New list horarios => "+listHorarios);

        if (resultadoH.isBlnValido()) {
            ListHorariosShow = listHorarios;
            if (ListHorariosShow.size() != 0)
                mostrarNotification(FacesMessage.SEVERITY_INFO, "INF:", "Lista de horarios cargada encontrada");
            else
                mostrarNotification(FacesMessage.SEVERITY_WARN, "ALERT:", "Lo sentimos no tenemos horarios para este dia, intenta con otro");
        } else
            mostrarNotification(FacesMessage.SEVERITY_WARN, "WARN:", "No se pudo obtener los horarios para esta fecha intenta con otra");
    }

    public String fechaFormatCita(){
        if (strCurrentCalendar != null){
            strLocalFecha = strCurrentCalendar;
            return MethodsGenerics.formatDate(strCurrentCalendar);
        } else
            return "Vació";
    }

    /**
     * @return String link con la direccion de los requisitos del trámite
     */
    public void requisitosTramite(){
        StringBuilder requisito = new StringBuilder();
        for (SelectItem tramite : listTramites){
            if (tramite.getValue().toString().equals(strCurrentTramite)) {
                String[] type = tramite.getDescription().split(",");
                if (type[0].equals("[text]")){
                    showlinktext = true;
                    showlinktramitereq = false;
                    type[0]="";
                    for (String s : type) {
                        requisito.append("-").append(s);
                    }
                }else {
                    requisito.append(tramite.getDescription());
                    showlinktext = false;
                    showlinktramitereq = true;
                }
            }
        }
        strRequisitos = requisito.toString();
    }

    /**
     * @apiNote Metodo para agendar la cita.
     */
    public void agendarCita(Long idhistorical){

        ResultadoTO flag1 = citaBusiness.validarTramite(idhistorical, Integer.parseInt(strCurrentTramite));
        ResultadoTO flag2 = citaBusiness.validarHorario(MethodsGenerics.formatDate(strLocalFecha), strCurrentHora);

        if (flag1.isBlnValido() && flag2.isBlnValido()){
            if ((boolean) flag1.getObjeto() &&  (boolean) flag2.getObjeto()){
                String[] strindate = strLocalFecha.split(" ");
                if (strindate[0].equals("Sat") || strindate[0].equals("Sun"))
                    mostrarNotification(FacesMessage.SEVERITY_WARN, "WARN:", "Lo sentimos los sabados y domingos no damos servicio intenta otro dia.");
                else{
                    Map<String, Object> valores = new HashMap<>();

                    valores.put("idhistorical", idhistorical);
                    valores.put("matricula",strLocalMatricula);
                    valores.put("idtramite",strCurrentTramite);
                    valores.put("idarea",strCurrentArea);
                    valores.put("descripcion",strMotivoCita);
                    valores.put("fecha",MethodsGenerics.formatDate(strLocalFecha));
                    valores.put("hora",strCurrentHora);

                    ResultadoTO resultadoTO = citaBusiness.guardarCita(valores);
                    String[] params1 = new String[]{ valores.get("fecha").toString(),valores.get("hora").toString(), "20181837"  };
                    String[] params2 = new String[]{ valores.get("idarea").toString(), "20181837" };
                    logger.info("--- CITAS RUN SAVE HORARIO ---");
                    ResultadoTO resultadoh1 = citaBusiness.reservarHorarios(params1, params2);
                    boolean flaghorarios = (boolean) resultadoh1.getObjeto();

                    if (resultadoTO.getObjeto().equals(true) && flaghorarios){
                        System.out.println("|------------------ WAS SUCCESFUL SAVE CITA => "+resultadoTO.getObjeto());
                        isCitaAgendada = true;
                        mostrarNotification(FacesMessage.SEVERITY_INFO, "INF:", "Tu cita se agendo correctamente, espera el comprobante");
                    }else   mostrarNotification(FacesMessage.SEVERITY_ERROR, "ERROR:", "No se pudo agendar tu cita");
                }
            } else {
                isForHidden = true;
                mostrarNotification(FacesMessage.SEVERITY_WARN, "ALERT:", "Ups! Parece que ya tienes una cita con este tramite.");
            }
        } else mostrarNotification(FacesMessage.SEVERITY_ERROR, "ERROR:", "Tenemos problemas con el servidor, intentalo mas tarde");
    }

    /**
     * @param severity FacesMessage severidad
     * @param title String title
     * @param msg String mensaje
     * TODO Metodo Temporal para las notificaciones.
     */
    public void mostrarNotification(FacesMessage.Severity severity, String title, String msg){
        FacesContext.getCurrentInstance().
                addMessage(null, new FacesMessage(severity, title, msg));
    }

    public void reload(){
        vHelp.redireccionar("/vistas/citas/nuevacita.uat");
    }

    public void regresar(){vHelp.redireccionar("/vistas/citas/cita.uat");}


    /*----------------------
     * @GettersAndSetters
     */

    public List<SelectItem> getListAreas() {
        return listAreas;
    }

    public void setListAreas(List<SelectItem> listAreas) {
        this.listAreas = listAreas;
    }

    public List<SelectItem> getListTramites() {
        return listTramites;
    }

    public void setListTramites(List<SelectItem> listTramites) {
        this.listTramites = listTramites;
    }

    public List<String> getListHorariosShow() {
        return ListHorariosShow;
    }

    public void setListHorariosShow(List<String> listHorariosShow) {
        ListHorariosShow = listHorariosShow;
    }

    public boolean isHasDataTramites() {
        return hasDataTramites;
    }

    public void setHasDataTramites(boolean hasDataTramites) {
        this.hasDataTramites = hasDataTramites;
    }

    public boolean isHasHorarios() {
        return hasHorarios;
    }

    public void setHasHorarios(boolean hasHorarios) {
        this.hasHorarios = hasHorarios;
    }

    public boolean isCitaAgendada() {
        return isCitaAgendada;
    }

    public void setCitaAgendada(boolean citaAgendada) {
        isCitaAgendada = citaAgendada;
    }

    public boolean isHasCalendar() {
        return hasCalendar;
    }

    public void setHasCalendar(boolean hasCalendar) {
        this.hasCalendar = hasCalendar;
    }

    public boolean isForHidden() {
        return isForHidden;
    }

    public void setForHidden(boolean forHidden) {
        isForHidden = forHidden;
    }

    public boolean isBtnLookNewCita() {
        return btnLookNewCita;
    }

    public void setBtnLookNewCita(boolean btnLookNewCita) {
        this.btnLookNewCita = btnLookNewCita;
    }

    public boolean isFlagHorarioReservado() {
        return flagHorarioReservado;
    }

    public void setFlagHorarioReservado(boolean flagHorarioReservado) {
        this.flagHorarioReservado = flagHorarioReservado;
    }

    public String getStrFechasDisableCalendar() {
        return strFechasDisableCalendar;
    }

    public void setStrFechasDisableCalendar(String strFechasDisableCalendar) {
        this.strFechasDisableCalendar = strFechasDisableCalendar;
    }

    public String getStrCurrentArea() {
        return strCurrentArea;
    }

    public void setStrCurrentArea(String strCurrentArea) {
        this.strCurrentArea = strCurrentArea;
    }

    public String getStrCurrentTramite() {
        return strCurrentTramite;
    }

    public void setStrCurrentTramite(String strCurrentTramite) {
        this.strCurrentTramite = strCurrentTramite;
    }

    public String getStrCurrentCalendar() {
        return strCurrentCalendar;
    }

    public void setStrCurrentCalendar(String strCurrentCalendar) {
        this.strCurrentCalendar = strCurrentCalendar;
    }

    public String getStrCurrentHora() {
        return strCurrentHora;
    }

    public void setStrCurrentHora(String strCurrentHora) {
        this.strCurrentHora = strCurrentHora;
    }

    public String getStrMotivoCita() {
        return strMotivoCita;
    }

    public void setStrMotivoCita(String strMotivoCita) {
        this.strMotivoCita = strMotivoCita;
    }

    public String getStrLocalNombreUser() {
        return strLocalNombreUser;
    }

    public void setStrLocalNombreUser(String strLocalNombreUser) {
        this.strLocalNombreUser = strLocalNombreUser;
    }

    public String getStrLocalMatricula() {
        return strLocalMatricula;
    }

    public void setStrLocalMatricula(String strLocalMatricula) {
        this.strLocalMatricula = strLocalMatricula;
    }

    public String getStrLocalArea() {
        return strLocalArea;
    }

    public void setStrLocalArea(String strLocalArea) {
        this.strLocalArea = strLocalArea;
    }

    public String getStrLocalTramite() {
        return strLocalTramite;
    }

    public void setStrLocalTramite(String strLocalTramite) {
        this.strLocalTramite = strLocalTramite;
    }

    public String getStrLocalFecha() {
        return strLocalFecha;
    }

    public void setStrLocalFecha(String strLocalFecha) {
        this.strLocalFecha = strLocalFecha;
    }

    public String getStrLocalHorario() {
        return strLocalHorario;
    }

    public void setStrLocalHorario(String strLocalHorario) {
        this.strLocalHorario = strLocalHorario;
    }

    public String getStrLocalDescripcion() {
        return strLocalDescripcion;
    }

    public void setStrLocalDescripcion(String strLocalDescripcion) {
        this.strLocalDescripcion = strLocalDescripcion;
    }

    public String getTomaxDate() {
        return tomaxDate;
    }

    public void setTomaxDate(String tomaxDate) {
        this.tomaxDate = tomaxDate;
    }

    public String getTominDate() {
        return tominDate;
    }

    public void setTominDate(String tominDate) {
        this.tominDate = tominDate;
    }

    public AreasBusiness getAreasBusiness() {
        return areasBusiness;
    }

    public void setAreasBusiness(AreasBusiness areasBusiness) {
        this.areasBusiness = areasBusiness;
    }

    public TramitesBusiness getTramitesBusiness() {
        return tramitesBusiness;
    }

    public void setTramitesBusiness(TramitesBusiness tramitesBusiness) {
        this.tramitesBusiness = tramitesBusiness;
    }

    public CitaBusiness getCitaBusiness() {
        return citaBusiness;
    }

    public void setCitaBusiness(CitaBusiness citaBusiness) {
        this.citaBusiness = citaBusiness;
    }

    public ResourceBundle getMsj() {
        return msj;
    }

    public void setMsj(ResourceBundle msj) {
        this.msj = msj;
    }

    public List<String> getListProgramaEdu() {
        return listProgramaEdu;
    }

    public void setListProgramaEdu(List<String> listProgramaEdu) {
        this.listProgramaEdu = listProgramaEdu;
    }

    public boolean isHasDataAreas() {
        return hasDataAreas;
    }

    public String getStrProgramaEdu() {
        return strProgramaEdu;
    }

    public void setStrProgramaEdu(String strProgramaEdu) {
        this.strProgramaEdu = strProgramaEdu;
    }

    public void setHasDataAreas(boolean hasDataAreas) {
        this.hasDataAreas = hasDataAreas;
    }

    public List<String> getListDatosAlumno() {
        return listDatosAlumno;
    }

    public void setListDatosAlumno(List<String> listDatosAlumno) {
        this.listDatosAlumno = listDatosAlumno;
    }

    public ConfiguacionesDTO getLista() {
        return listaConfig;
    }

    public void setLista(ConfiguacionesDTO lista) {
        this.listaConfig = lista;
    }

    public boolean isShowDataEstudiante() {
        return showDataEstudiante;
    }

    public boolean isShowlinktramitereq() {
        return showlinktramitereq;
    }

    public void setShowlinktramitereq(boolean showlinktramitereq) {
        this.showlinktramitereq = showlinktramitereq;
    }

    public boolean isShowlinktext() {
        return showlinktext;
    }

    public void setShowlinktext(boolean showlinktext) {
        this.showlinktext = showlinktext;
    }

    public String getStrRequisitos() {
        return strRequisitos;
    }

    public void setStrRequisitos(String strRequisitos) {
        this.strRequisitos = strRequisitos;
    }

    public void setShowDataEstudiante(boolean showDataEstudiante) {
        this.showDataEstudiante = showDataEstudiante;
    }
}

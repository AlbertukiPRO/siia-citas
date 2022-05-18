package mx.uatx.siia.logeo.controlador;

import com.ibm.icu.text.SimpleDateFormat;
import com.sun.istack.NotNull;
import mx.uatx.siia.citas.CitasHelper;
import mx.uatx.siia.citas.ServicesCitas;
import mx.uatx.siia.citas.modelo.Areas.areasBusiness.areaBusiness;
import mx.uatx.siia.citas.modelo.Tramites.tramitesBusiness.tramiteBusiness;
import mx.uatx.siia.citas.modelo.citasBusiness.citaBusiness;
import mx.uatx.siia.comun.helper.VistasHelper;
import mx.uatx.siia.serviciosUniversitarios.dto.AreasTO;
import mx.uatx.siia.serviciosUniversitarios.dto.ResultadoTO;
import mx.uatx.siia.serviciosUniversitarios.dto.TramitesTO;
import mx.uatx.siia.serviciosUniversitarios.enums.SeveridadMensajeEnum;
import org.primefaces.behavior.ajax.AjaxBehavior;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.*;


@ManagedBean(name = "cita")
@SessionScoped
public class Citas implements Serializable {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2621529818431646329L;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @ManagedProperty("#{citaBusiness}")
    private citaBusiness citaBusiness;

    @ManagedProperty("#{msj}")
    private ResourceBundle msj;

    private final VistasHelper vHelp = new VistasHelper();

    // ATRIBUTOS DEl Objeto CITA.

    private Integer intIdCita;
    private Integer intIdAlumno;
    private Integer intIdArea;
    private Integer intIdTramite;
    private String strDescripcionTramite;
    private String strEstatus;
    private String strFechaReserva;
    private String strRetroalimentacion;
    private String strHoraReservada;
    public String strIdString;

    //Datos del alumno
    private String localstrMatricula;

    public String getLocalstrMatricula() {
        return localstrMatricula;
    }

    public void setLocalstrMatricula(String localstrMatricula) {
        this.localstrMatricula = localstrMatricula;
    }

    public String getLocalstrNombre() {
        return localstrNombre;
    }

    public void setLocalstrNombre(String localstrNombre) {
        this.localstrNombre = localstrNombre;
    }

    public String getLocalfacultdad() {
        return localfacultdad;
    }

    public void setLocalfacultdad(String localfacultdad) {
        this.localfacultdad = localfacultdad;
    }

    public String getLocalprogramaEducativo() {
        return localprogramaEducativo;
    }

    public void setLocalprogramaEducativo(String localprogramaEducativo) {
        this.localprogramaEducativo = localprogramaEducativo;
    }

    private String localstrNombre;
    private String localfacultdad;
    private String localprogramaEducativo;

    //USO DE LA INTERFAZ
    @NotNull
    private String listaDatosAlumn = null;
    @NotNull
    private String listaDatosAreas = null;
    @NotNull
    private String listaDatosTramites = null;
    private final List<String> listDatosAlumno;
    @NotNull
    private String strcalendarValue;
    @NotNull
    private String strHoraValue;
    @NotNull
    private String strDescripcion;

    private String fechasDisable;
    private Map<String,String> listAreas;
    private List<String> listTramites;
    private List<String> listHorarios;

    public boolean isRenderDatosAlumno() {
        return renderDatosAlumno;
    }

    public void setRenderDatosAlumno(boolean renderDatosAlumno) {
        this.renderDatosAlumno = renderDatosAlumno;
    }

    private boolean renderDatosAlumno;

    public void updateValues(String nombre, String matricula){

        //TODO: Obtener los valores de la DB.

        localstrNombre = nombre;
        localstrMatricula = matricula;
        localprogramaEducativo = "Ingeniería en Computación";
        localfacultdad = "Facultad de Ciencias Basicas Ingeniería y Tecnologia";

        renderDatosAlumno = true;


        System.out.println("Cambiando los valores");
    }

    private CitasHelper citasHelper;

    public Citas()  {
        citasHelper  = new CitasHelper();
        listDatosAlumno = Arrays.asList("[20181837] LICENCIATURA EN INGENIERÍA EN COMPUTACIÓN CAMPUS APIZACO (2018)");
        obtenerAreas();
        renderDatosAlumno = false;
        logger.info("Bean Citas { Constructor() }");
    }

    private List<String> FromSerGetHorariosRe(){
        System.out.println("------ GET HORARIOS FROM DB  => [ Run ]");
        List<String> horariosReservados = ServicesCitas.getHorariosAPI("http://localhost/siiaServices/apis/getFechasReservadas.php", strFechaReserva);
        return horariosReservados;
    }

    public String getFechasInhabiles(){

        System.out.println("----- GET FECHAS INHÁBILES");
        StringBuilder cadena = new StringBuilder();
        List<String> value = ServicesCitas.getFechasInabilesAPI("http://localhost/siiaServices/apis/getFechasReservadas.php",""+listaDatosAreas.replace(" ","%20"));

        for (String item: value){
            cadena.append("'").append(item).append("',");
        }
        return removeLastChar(String.valueOf(cadena));
    }

    public static String removeLastChar(String s) {
        return (s == null || s.length() == 0)
                ? null
                : (s.substring(0, s.length() - 1));
    }

    public List<SelectItem> obtenerAreas(){
        System.out.println("---- GET DATA OF AREAS");
        return citasHelper.getSelectAreas();
    }

    public void obtenerTramites()  {
        System.out.println("---- GET DATA OF TRAMITES");
        if(listaDatosAreas!=null){
            List<TramitesTO> tramites = ServicesCitas.getTramitesAPI("http://localhost/siiaServices/apis/getTramites.php",""+listaDatosAreas.replace(" ","%20"));
            listTramites = new ArrayList<>();
            try {
                System.out.println("--- TRAMITES LIST<TramitesTO>");
                tramites.forEach((e)-> System.out.println(e.getStrNombreTramite()));
                tramites.forEach((i)->{
                    listTramites.add(i.getStrNombreTramite());
                });
            }catch (Exception e){
                System.out.println(e);
            }
            fechasDisable = getFechasInhabiles();
            vHelp.redireccionar("/vistas/citas/cita.uat");

        }else{
            listTramites = Arrays.asList("Seleccione");
        }
    }
    public boolean showTramites(){
        if(listaDatosAreas!=null){
            return true;
        }else{
            return false;
        }
    }

    public List<String> generarHorarios(int horaInicio, int HoraFin, int DuracionCitas, List<String> horariosReservados){

        List<String> listHorarios = new ArrayList<String>();

        int hora = horaInicio;
        int minuto = 0;
        int residuo = 0;
        while (hora<HoraFin){
            if ( minuto < 59 && hora != HoraFin){
                String item = formatHora(Integer.toString(hora))+":"+formatHora(Integer.toString(minuto));
                listHorarios.add(item);
            }
            if(minuto<=60){
                minuto+=DuracionCitas;
            }else{
                residuo=minuto-60;
                minuto=residuo;
                hora+=1;
            }
        }
        horariosReservados.stream().forEach((item)->{
            for (int i = 0; i < listHorarios.size(); i++) {
                if (item.equals(listHorarios.get(i))){
                    listHorarios.remove(i);
                }
            }
        });

        return listHorarios;
    }

    public String formatHora(String number){
        return number.length() == 1 ? "0"+number : number;
    }

//    public String obtenerFechasInhabil(){
//        return "'5/10/2022','5/16/2022'";
//    }

    public void ComprobarFecha(){
        //TODO: Checar la disponibilidad.
        System.out.println("------------Comprobando las disponibilidad de las fechas-----------");


        listHorarios = generarHorarios(8,13,25, FromSerGetHorariosRe());

        final ResultadoTO res = new ResultadoTO();
        res.agregarMensaje(SeveridadMensajeEnum.ALERTA, "comun.label.sumario.msj.iniciar.sesion");
        vHelp.pintarMensajes(msj, res);
    }

    public void AgendarCita(){
        System.out.println("----------------Agendar Cita---------\n");

        Map<String, String> valores = new HashMap<String, String>();

        valores.put("matricula",localstrMatricula);
        valores.put("idtramite",listaDatosTramites);
        valores.put("idarea",listaDatosAreas);
        valores.put("descripcion",strDescripcion);
        valores.put("fecha",strcalendarValue);
        valores.put("hora",strHoraValue);

        int codeResponse = ServicesCitas.addValues("http://localhost/siiaServices/apis/Insert.php",valores);
    }

    public String getStrDescripcion() {
        return strDescripcion;
    }
    public void setStrDescripcion(String strDescripcion) {
        this.strDescripcion = strDescripcion;
    }
    public String getStrHoraValue() {
        return strHoraValue;
    }

    public void setStrHoraValue(String strHoraValue) {
        this.strHoraValue = strHoraValue;
    }

    public List<String> getListHorarios() {
        return listHorarios;
    }

    public void setListHorarios(List<String> listHorarios) {
        this.listHorarios = listHorarios;
    }


    public String getFechasDisable() {
        return fechasDisable;
    }

    public void setFechasDisable(String fechasDisable) {
        this.fechasDisable = fechasDisable;
    }

    public String getStrcalendarValue() {
        return strcalendarValue;
    }

    public void setStrcalendarValue(String strcalendarValue) {
        this.strcalendarValue = strcalendarValue;
    }
    public String getListaDatosTramites() {
        return listaDatosTramites;
    }

    public void setListaDatosTramites(String listaDatosTramites) {
        this.listaDatosTramites = listaDatosTramites;
    }

    public String getListaDatosAlumn() {
        return listaDatosAlumn;
    }

    public void setListaDatosAlumn(String listaDatosAlumno) {
        this.listaDatosAlumn = listaDatosAlumno;
    }

    public Map<String, String> getListAreas() {
        return listAreas;
    }

    public void setListAreas(Map<String, String> listAreas) {
        this.listAreas = listAreas;
    }

    public List<String> getListDatosAlumno(){
        return listDatosAlumno;
    }


    public List<String> getListTramites() {
        return listTramites;
    }

    public void setListTramites(List<String> listTramites) {
        this.listTramites = listTramites;
    }

    public Integer getIntIdCita() {
        return intIdCita;
    }

    public void setIntIdCita(Integer intIdCita) {
        this.intIdCita = intIdCita;
    }

    public Integer getIntIdAlumno() {
        return intIdAlumno;
    }

    public void setIntIdAlumno(Integer intIdAlumno) {
        this.intIdAlumno = intIdAlumno;
    }

    public Integer getIntIdArea() {
        return intIdArea;
    }

    public void setIntIdArea(Integer intIdArea) {
        this.intIdArea = intIdArea;
    }

    public Integer getIntIdTramite() {
        return intIdTramite;
    }

    public void setIntIdTramite(Integer intIdTramite) {
        this.intIdTramite = intIdTramite;
    }

    public String getStrDescripcionTramite() {
        return strDescripcionTramite;
    }

    public void setStrDescripcionTramite(String strDescripcionTramite) {
        this.strDescripcionTramite = strDescripcionTramite;
    }

    public String getStrEstatus() {
        return strEstatus;
    }

    public void setStrEstatus(String strEstatus) {
        this.strEstatus = strEstatus;
    }

    public String getStrFechaReserva() {
        return strFechaReserva;
    }

    public void setStrFechaReserva(String strFechaReserva) {
        this.strFechaReserva = strFechaReserva;
    }

    public String getStrRetroalimentacion() {
        return strRetroalimentacion;
    }

    public void setStrRetroalimentacion(String strRetroalimentacion) {
        this.strRetroalimentacion = strRetroalimentacion;
    }

    public String getStrHoraReservada() {
        return strHoraReservada;
    }

    public void setStrHoraReservada(String strHoraReservada) {
        this.strHoraReservada = strHoraReservada;
    }

    /*
    *   Propiedad set para la inyección
    *   @param CitaBusiness
    */
    public citaBusiness getCitaBusiness() {
        return citaBusiness;
    }

    public void setCitaBusiness(citaBusiness citaBusiness) {
        this.citaBusiness = citaBusiness;
    }

    public ResourceBundle getMsj() {
        return msj;
    }

    public void setMsj(ResourceBundle msj) {
        this.msj = msj;
    }

    public String getListaDatosAreas() {
        return listaDatosAreas;
    }

    public void setListaDatosAreas(String listaDatosAreas) {
        this.listaDatosAreas = listaDatosAreas;
    }
}

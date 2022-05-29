package mx.uatx.siia.logeo.controlador;

import com.sun.istack.NotNull;
import mx.uatx.siia.citas.CitasHelper;
import mx.uatx.siia.citas.ServicesCitas;
import mx.uatx.siia.citas.modelo.citasBusiness.citaBusiness;
import mx.uatx.siia.citas.pruebas.Prueba;
import mx.uatx.siia.comun.helper.VistasHelper;
import mx.uatx.siia.reportes.GenerarReporte;
import mx.uatx.siia.serviciosUniversitarios.dto.ResultadoTO;
import mx.uatx.siia.serviciosUniversitarios.enums.SeveridadMensajeEnum;
import net.sf.jasperreports.engine.JRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.*;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;


@ManagedBean(name = "cita")
@RequestScoped
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
    @NotNull
    private String fechasDisable;
    private Map<String,String> listAreas;
    private List<String> listTramites;
    private List<String> listHorarios;

    public boolean isRenderDatosAlumno() {
        return renderDatosAlumno;
    }

    private boolean isAgendada;


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
    private GenerarReporte generarReporte;

    public Citas()  {
        citasHelper  = new CitasHelper();
        generarReporte = new GenerarReporte();
        listDatosAlumno = Arrays.asList("[20181837] LICENCIATURA EN INGENIERÍA EN COMPUTACIÓN CAMPUS APIZACO (2018)");
        obtenerAreas();
        renderDatosAlumno = false;
        logger.info("Bean Citas { Constructor() }");
    }

    public void ExportarPDF() throws JRException {

        List<Prueba> lista = new ArrayList<Prueba>();
        lista.add(new Prueba("20181837","Alberto"));

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put("strMatricula","2018183");
        parametros.put("strNombre","Alberto Noche Rosas");

        vHelp.llenarYObtenerBytesReporteJasperPDF("src/main/webapp","Reporte.jasper", lista, (HashMap<String, Object>) parametros);

//        Map<String, Object> parametros = new HashMap<String, Object>();
//        parametros.put("strMatricula","2018183");
//        parametros.put("strNombre","Alberto Noche Rosas");
//
//        File jasperFile = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Reporte.jasper"));
//
//        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperFile.getPath(), parametros);
//
//        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
//        response.addHeader("Content-Disposition","attachment;filename=ReporteCita.pdf");
//        ServletOutputStream stream = response.getOutputStream();
//
//        JasperExportManager.exportReportToPdfStream(jasperPrint, stream);
//
//        stream.flush();
//        stream.close();
//        FacesContext.getCurrentInstance().responseComplete();
    }

    private List<String> FromSerGetHorariosRe(){
        System.out.println("------ GET HORARIOS FROM DB  => [ Run ]");
        List<String> horariosReservados = ServicesCitas.getHorariosAPI("http://localhost/siiaServices/apis/getFechasReservadas.php",strFechaReserva,listaDatosAreas);
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

    public List<SelectItem> obtenerTramites()  {
        System.out.println("---- GET DATA OF TRAMITES");
        if (listaDatosAreas != null){
            fechasDisable = getFechasInhabiles();
            return citasHelper.getSelectTramites(listaDatosAreas);
        }else{
            return citasHelper.getSelectTramites("1");
        }

    }

    public void ComprobarFecha(){
        System.out.println("------------Comprobando las disponibilidad de las fechas-----------");

        listHorarios = CitasHelper.generarHorarios(8,13,25, FromSerGetHorariosRe());

        final ResultadoTO res = new ResultadoTO();
        res.agregarMensaje(SeveridadMensajeEnum.INFO, "comun.msj.citas.fechas.ok");
        vHelp.pintarMensajes(msj, res);

        setStrcalendarValue(strcalendarValue);
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

        System.out.println("PUT [ Save Cita ] WITH -> "+valores);

        int codeResponse = ServicesCitas.addValues("http://localhost/siiaServices/apis/Insert.php",valores);

        HttpServletResponse res = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String uri = req.getRequestURI();
        String url = "http://localhost/siiaServices/reportes/generarPDF.php?id="+localstrNombre+","+localstrMatricula;
        try {
            res.getWriter().println("<script>open('" + url + "','_blank', 'location=yes,height=600,width=800,scrollbars=yes,status=yes');</script>");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FacesContext.getCurrentInstance().responseComplete();

        if (codeResponse == 1){
            isAgendada = true;
            pintarMensajeCitaAgendada();
        }
    }

    public void pintarMensajeCitaAgendada(){
        if (isAgendada) {
            final ResultadoTO res = new ResultadoTO();
            res.agregarMensaje(SeveridadMensajeEnum.INFO, "comun.msj.citas.citas.ok");
            vHelp.pintarMensajes(msj, res);
        }
    }

    public boolean isAgendada() {
        return isAgendada;
    }

    public void setAgendada(boolean agendada) {
        isAgendada = agendada;
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
        this.strcalendarValue = CitasHelper.formatDate(strcalendarValue);
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

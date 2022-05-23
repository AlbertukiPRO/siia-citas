package mx.uatx.siia.citas;

import mx.uatx.siia.comun.helper.VistasHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ResourceBundle;

@ManagedBean(name = "misictas")
@SessionScoped
public class MisCitasBean implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2621529818431646329L;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @ManagedProperty("#{msj}")
    private ResourceBundle msj;

    private final VistasHelper vHelp = new VistasHelper();

    /*LOCAL ATTRIBUTES*/

    private String idUser;
    private boolean renderMisCitas;
    private String strLocalMatricula;
    private String strLocalNombre;


    public MisCitasBean(){
        System.out.println("Build Constructor Mis Citas");
        renderMisCitas = false;
    }

    public void UpdateValores(String nombre, String matricula){
        renderMisCitas = true;
        strLocalMatricula = matricula;
        strLocalNombre = nombre;
    }

    public void GetCitas(){
        System.out.println("--- GET CITAS ---");
    }

    public boolean IsRender(){
        return true;
    }


    public ResourceBundle getMsj() {
        return msj;
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


}

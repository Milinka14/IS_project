/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entiteti;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ivan
 */
@Entity
@Table(name = "pripada")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Pripada.findAll", query = "SELECT p FROM Pripada p"),
    @NamedQuery(name = "Pripada.findById", query = "SELECT p FROM Pripada p WHERE p.id = :id")})
public class Pripada implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "idA", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private AudioSnimak idA;
    @JoinColumn(name = "idK", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Kategorija idK;

    public Pripada() {
    }

    public Pripada(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AudioSnimak getIdA() {
        return idA;
    }

    public void setIdA(AudioSnimak idA) {
        this.idA = idA;
    }

    public Kategorija getIdK() {
        return idK;
    }

    public void setIdK(Kategorija idK) {
        this.idK = idK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Pripada)) {
            return false;
        }
        Pripada other = (Pripada) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entiteti.Pripada[ id=" + id + " ]";
    }
    
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entiteti;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author ivan
 */
@Entity
@Table(name = "audio_snimak")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AudioSnimak.findAll", query = "SELECT a FROM AudioSnimak a"),
    @NamedQuery(name = "AudioSnimak.findById", query = "SELECT a FROM AudioSnimak a WHERE a.id = :id"),
    @NamedQuery(name = "AudioSnimak.findByNaziv", query = "SELECT a FROM AudioSnimak a WHERE a.naziv = :naziv"),
    @NamedQuery(name = "AudioSnimak.findByTrajanje", query = "SELECT a FROM AudioSnimak a WHERE a.trajanje = :trajanje"),
    @NamedQuery(name = "AudioSnimak.findByIdK", query = "SELECT a FROM AudioSnimak a WHERE a.idK = :idK"),
    @NamedQuery(name = "AudioSnimak.findByDatumPostavljen", query = "SELECT a FROM AudioSnimak a WHERE a.datumPostavljen = :datumPostavljen")})
public class AudioSnimak implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "naziv")
    private String naziv;
    @Basic(optional = false)
    @NotNull
    @Column(name = "trajanje")
    private int trajanje;
    @Basic(optional = false)
    @NotNull
    @Column(name = "idK")
    private int idK;
    @Column(name = "datumPostavljen")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datumPostavljen;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idA")
    private List<Pripada> pripadaList;

    public AudioSnimak() {
    }

    public AudioSnimak(Integer id) {
        this.id = id;
    }

    public AudioSnimak(Integer id, String naziv, int trajanje, int idK) {
        this.id = id;
        this.naziv = naziv;
        this.trajanje = trajanje;
        this.idK = idK;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public int getTrajanje() {
        return trajanje;
    }

    public void setTrajanje(int trajanje) {
        this.trajanje = trajanje;
    }

    public int getIdK() {
        return idK;
    }

    public void setIdK(int idK) {
        this.idK = idK;
    }

    public Date getDatumPostavljen() {
        return datumPostavljen;
    }

    public void setDatumPostavljen(Date datumPostavljen) {
        this.datumPostavljen = datumPostavljen;
    }

    @XmlTransient
    public List<Pripada> getPripadaList() {
        return pripadaList;
    }

    public void setPripadaList(List<Pripada> pripadaList) {
        this.pripadaList = pripadaList;
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
        if (!(object instanceof AudioSnimak)) {
            return false;
        }
        AudioSnimak other = (AudioSnimak) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entiteti.AudioSnimak[ id=" + id + " ]";
    }
    
}

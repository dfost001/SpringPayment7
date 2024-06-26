package model;
// Generated May 5, 2015 7:47:34 PM by Hibernate Tools 4.3.1


import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Film generated by hbm2java
 */
@Entity
@Table(name="film"
    ,catalog="sakila_2"
)
public class Film  implements java.io.Serializable {


     private Short filmId;
     private Language languageByLanguageId;
     private Language languageByOriginalLanguageId;
     private String title;
     private String description;
     private Date releaseYear;
     private byte rentalDuration;
     private BigDecimal rentalRate;
     private Short length;
     private BigDecimal replacementCost;
     private String rating;
     private String specialFeatures;
     private Date lastUpdate;
     private Set<FilmActor> filmActors = new HashSet<>(0);
     private Set<FilmCategory> filmCategories = new HashSet<>(0);

    public Film() {
    }

	
    public Film(Language languageByLanguageId, String title, byte rentalDuration, BigDecimal rentalRate, BigDecimal replacementCost, Date lastUpdate) {
        this.languageByLanguageId = languageByLanguageId;
        this.title = title;
        this.rentalDuration = rentalDuration;
        this.rentalRate = rentalRate;
        this.replacementCost = replacementCost;
        this.lastUpdate = lastUpdate;
    }
    
   
    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="film_id", unique=true, nullable=false)
    public Short getFilmId() {
        return this.filmId;
    }
    
    public void setFilmId(Short filmId) {
        this.filmId = filmId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="language_id", nullable=false)
    public Language getLanguageByLanguageId() {
        return this.languageByLanguageId;
    }
    
    public void setLanguageByLanguageId(Language languageByLanguageId) {
        this.languageByLanguageId = languageByLanguageId;
    }

@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="original_language_id")
    public Language getLanguageByOriginalLanguageId() {
        return this.languageByOriginalLanguageId;
    }
    
    public void setLanguageByOriginalLanguageId(Language languageByOriginalLanguageId) {
        this.languageByOriginalLanguageId = languageByOriginalLanguageId;
    }

    
    @Column(name="title", nullable=false)
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    
    @Column(name="description", length=65535)
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="release_year", length=0)
    public Date getReleaseYear() {
        return this.releaseYear;
    }
    
    public void setReleaseYear(Date releaseYear) {
        this.releaseYear = releaseYear;
    }

    
    @Column(name="rental_duration", nullable=false)
    public byte getRentalDuration() {
        return this.rentalDuration;
    }
    
    public void setRentalDuration(byte rentalDuration) {
        this.rentalDuration = rentalDuration;
    }

    
    @Column(name="rental_rate", nullable=false, precision=4)
    public BigDecimal getRentalRate() {
        return this.rentalRate;
    }
    
    public void setRentalRate(BigDecimal rentalRate) {
        this.rentalRate = rentalRate;
    }

    
    @Column(name="length")
    public Short getLength() {
        return this.length;
    }
    
    public void setLength(Short length) {
        this.length = length;
    }

    
    @Column(name="replacement_cost", nullable=false, precision=5)
    public BigDecimal getReplacementCost() {
        return this.replacementCost;
    }
    
    public void setReplacementCost(BigDecimal replacementCost) {
        this.replacementCost = replacementCost;
    }

    
    @Column(name="rating", length=5)
    public String getRating() {
        return this.rating;
    }
    
    public void setRating(String rating) {
        this.rating = rating;
    }

    
    @Column(name="special_features", length=54)
    public String getSpecialFeatures() {
        return this.specialFeatures;
    }
    
    public void setSpecialFeatures(String specialFeatures) {
        this.specialFeatures = specialFeatures;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="last_update", nullable=false, length=19)
    public Date getLastUpdate() {
        return this.lastUpdate;
    }
    
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

@OneToMany(fetch=FetchType.EAGER, mappedBy="film")
    public Set<FilmActor> getFilmActors() {
        return this.filmActors;
    }
    
    public void setFilmActors(Set<FilmActor> filmActors) {
        this.filmActors = filmActors;
    }

@OneToMany(fetch=FetchType.EAGER, mappedBy="film")
    public Set<FilmCategory> getFilmCategories() {
        return this.filmCategories;
    }
    
    public void setFilmCategories(Set<FilmCategory> filmCategories) {
        this.filmCategories = filmCategories;
    }




}



package eu.ensg.ing19.pointofinterest.dataobject;

public class PointOfInterest extends DataBaseObject {

    private String title;
    private String description;
    private Double lat;
    private Double lng;
    private Long userId;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public Long getUserId() {
        return userId;
    }

    public PointOfInterest(String title, String description, Double lat, Double lng, Long userId) {
        this.title = title;
        this.description = description;
        this.lat = lat;
        this.lng = lng;
        this.userId = userId;
    }

    public PointOfInterest(long id, String title, String description, Double lat, Double lng, Long userId) {
        super(id);
        this.title = title;
        this.description = description;
        this.lat = lat;
        this.lng = lng;
        this.userId = userId;
    }

}

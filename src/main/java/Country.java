public class Country {
    //    Country or area,Subregion,Region,Internet users,Population
    protected String nameCountry;
    protected String subregion;
    protected String region;
    protected int internetUsers;
    protected int population;

    public Country(String country, String subregion, String region, int InternetUsers, int Population) {
        this.nameCountry = country;
        this.subregion = subregion;
        this.region = region;
        this.internetUsers = InternetUsers;
        this.population = Population;
    }

    public String getSubregion() {
        return subregion;
    }

    public String getRegion() {
        return region;
    }

    public int getInternetUsers() {
        return internetUsers;
    }

    public int getPopulation() {
        return population;
    }

    public String getNameCountry() {
        return nameCountry;
    }

    @Override
    public String toString() {
        return "Country{" +
                "country='" + nameCountry + '\'' +
                ", subregion='" + subregion + '\'' +
                ", region='" + region + '\'' +
                ", internetUsers=" + internetUsers +
                ", population=" + population +
                "} \n";
    }

}

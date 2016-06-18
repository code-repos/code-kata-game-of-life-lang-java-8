/**
 * Created by Philip Schwarz on 18/06/2016.
 */
public class Location implements Comparable<Location>
{
    public Location(int row, int column)
    {
        this.row = row;
        this.col = column;
    }

    public Location west() { return new Location(row, col - 1); }
    public Location northWest() { return new Location(row - 1, col - 1); }
    public Location north() { return new Location(row - 1, col); }
    public Location northEast() { return new Location(row - 1, col + 1); }
    public Location east() { return new Location(row, col + 1); }
    public Location southEast() { return new Location(row + 1, col + 1); }
    public Location south() { return new Location(row + 1, col); }
    public Location southWest() { return new Location(row + 1, col - 1); }

    /**
     * A location comes before locations that are:
     * 1) South of it
     * 2) Neither north of it nor south of it but east of it
     */
    @Override
    public int compareTo(Location other)
    {
        if (isNorthOf(other) || (isWestOf(other) && !isSouthOf(other)))
            return -1;
        else if (isSouthOf(other) || (isEastOf(other) && !isNorthOf(other)))
            return +1;
        else
            return 0;
    }

    /**
     * The number of horizontal locations (or vertical locations, if greater)
     * between this location and another location.
     */
    public int distanceFrom(Location other)
    {
        int numberOfRowsBetweenLocations = Math.abs(Math.abs(this.row) - Math.abs(other.row));
        int numberOfColumnsBetweenLocations = Math.abs(Math.abs(this.col) - Math.abs(other.col));
        return Math.max(numberOfRowsBetweenLocations, numberOfColumnsBetweenLocations);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (row != location.row) return false;
        return col == location.col;

    }

    @Override
    public int hashCode()
    {
        int result = row;
        result = 31 * result + col;
        return result;
    }

    @Override
    public String toString()
    {
        return "Location{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }

    public boolean isEastOf(Location other)
    {
        return this.col > other.col;
    }

    public boolean isSouthOf(Location other)
    {
        return this.row > other.row;
    }

    public boolean isWestOf(Location other)
    {
        return this.col < other.col;
    }

    public boolean isNorthOf(Location other)
    {
        return this.row < other.row;
    }

    private final int row;
    private final int col;
}

/**
 * Created by Philip Schwarz on 18/06/2016.
 */

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class World
{
    public World evolve()
    {
        return new World( locationsOfSurvivingCells(), locationsOfNewbornCells() );
    }

    // Creation Methods ///////////////////////////////////////////////////////////////////////////////////////////

    public static World empty() { return new World(); }

    public static World withCellsAt(Location... locations) { return new World(Arrays.asList(locations)); }

    public static World withCellsAt(List<Location>... groupsOfLocations) { return new World(groupsOfLocations); }

    // Predicates /////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean hasLiveCellAt(Location location)
    {
        return liveCellLocations.contains(location);
    }

    public boolean isEmpty()
    {
        return liveCellLocations.isEmpty();
    }

    // Overridden Object methods //////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        World world = (World) o;

        return liveCellLocations.equals(world.liveCellLocations);

    }

    @Override
    public int hashCode()
    {
        return liveCellLocations.hashCode();
    }

    @Override
    public String toString()
    {
        return toString( buildGridOfLocations() );
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Private ////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Member fields //////////////////////////////////////////////////////////////////////////////////////////////
    private final HashSet<Location> liveCellLocations;

    // Constructors ///////////////////////////////////////////////////////////////////////////////////////////////
    private World(List<Location>... groupsOfLocations){

        liveCellLocations = new HashSet<>();

        stream(groupsOfLocations)
                .flatMap(List::stream)
                .forEach(liveCellLocations::add);
    }

    // Location of Surviving Cells ////////////////////////////////////////////////////////////////////////////////

    private List<Location> locationsOfSurvivingCells()
    {
        return liveCellLocations
          .stream()
          .filter(aCellWillSurviveAt)
          .collect(toList());
    }

    private Predicate<Location> aCellWillSurviveAt = location ->
        NEIGHBOUR_COUNTS_ALLOWING_SURVIVAL.contains(
            countOfNeighbouringCellsAt(location));

    private long countOfNeighbouringCellsAt(Location location)
    {
        return neighboursOf(location)
                .stream()
                .filter(isPopulated)
                .count();
    }

    // Locations of Newborn Cells /////////////////////////////////////////////////////////////////////////////////

    private List<Location> locationsOfNewbornCells() {
      return
        liveCellLocations.stream().flatMap( l1 ->
          liveCellLocations.stream().flatMap( l2 ->
            liveCellLocations.stream().flatMap( l3 ->
              areInNeighbourhoodOfSomeCell(l1, l2, l3)
              ? emptyLocationsWithLiveNeighboursInAllAndOnlyLocations(l1, l2, l3)
              : Stream.empty())))
        .collect(toList());
    }

    private boolean areInNeighbourhoodOfSomeCell(Location... locations) {
      return distinct(locations) && distantAtMostOneCell(locations);
    }

    private boolean distinct(Location... locations) {
        long numberOfLocations = locations.length;
        long numberOfDistinctLocations = stream(locations).distinct().count();
        return numberOfDistinctLocations == numberOfLocations;
    }

    private boolean distantAtMostOneCell(Location... locations) {
      return
        stream(locations).allMatch( l1 ->
          stream(locations).allMatch( l2 ->
            l1.distanceFrom(l2) <= 2));
    }

    private Stream<Location> emptyLocationsWithLiveNeighboursInAllAndOnlyLocations(Location... locations) {
        return
            emptyLocationsThatAreSharedNeighboursOf(locations)
            .stream()
            .filter(hasNeighbouringLiveCellsInAllAndOnlyThese(locations));

    }

    private List<Location> emptyLocationsThatAreSharedNeighboursOf(Location[] locations)
    {
        return
            stream(locations)
            .map(emptyLocationsThatAreNeighboursOf)
            .reduce(intersection)
            .orElseGet(Collections::emptyList);
    }

    private Predicate<Location> isPopulated = this::hasLiveCellAt;
    private Predicate<Location> isNotPopulated =  isPopulated.negate();

    private Function<Location,List<Location>> emptyLocationsThatAreNeighboursOf = location ->
            neighboursOf(location)
            .stream()
            .filter(isNotPopulated)
            .collect(toList());

    private BinaryOperator<List<Location>> intersection = (xs, ys) ->
        xs.stream().filter(ys::contains).collect(toList());

    private Predicate<Location> hasNeighbouringLiveCellsInAllAndOnlyThese(Location... locations) {
        return location -> areTheSameLocations(liveNeighboursOf(location), locations);
    }

    private boolean areTheSameLocations(List<Location> locations, Location[] otherLocations)
    {
        Set<Location> setOne = new HashSet<>(locations);
        Set<Location> setTwo = stream(otherLocations).collect(toSet());
        return setOne.equals(setTwo);
    }

    private List<Location> liveNeighboursOf(Location location) {
        return
            neighboursOf(location)
            .stream()
            .filter(isPopulated)
            .collect(toList());
    }

    private List<Location> neighboursOf(Location location) {
      return asList(
          location.northWest(), location.north(), location.northEast(),
          location.west(),      /*  location  */  location.east(),
          location.southWest(), location.south(), location.southEast());
    }

    // Displaying Worlds ///////////////////////////////////////////////////////////////////////////////////////////////

    private String toString(List<List<Location>> grid)
    {
        return grid
                .stream()
                .map(convertRowOfLocationsToString)
                .collect(joining());
    }

    private Function<List<Location>, String> convertRowOfLocationsToString = row ->
        row.stream()
                .map(location -> liveCellPresentAt(location) ? "O" : "_")
                .collect(joining(NO_DELIMITER, NO_PREFIX, NEWLINE_SUFFIX));

    private boolean liveCellPresentAt(Location location)
    {
        return hasLiveCellAt(location);
    }

    private List<List<Location>> buildGridOfLocations()
    {
        List<Location> northMostFirst = liveCellLocations.stream().sorted().collect(toList());
        Location northMost = northMostFirst.get(0);
        Location southMost = northMostFirst.get(northMostFirst.size()-1);

        List<Location> westMostFirst = liveCellLocations.stream().sorted(byWestMostFirst).collect(toList());
        Location westMost = westMostFirst.get(0);
        Location eastMost = westMostFirst.get(westMostFirst.size() - 1);

        Location topLeft = getTopLeft(northMost, westMost);
        Location topRight = getTopRight(northMost, eastMost);
        Location bottomLeft = getBottomLeft(northMost, southMost);

        List<Location> topRow = topRowWith(topLeft, topRight);

        return gridWith(topRow, topLeft, bottomLeft);
    }

    private static final Comparator<? super Location> byWestMostFirst = (aLocation, anotherLocation) ->
        aLocation.isWestOf(anotherLocation)
        ? -1
        : (aLocation.isEastOf(anotherLocation)
           ? 1
           : 0);

    private static final Location getBottomLeft(Location northMost, Location southMost)
    {
        return Stream.iterate(northMost,Location::south)
                .filter(location -> !southMost.isSouthOf(location))
                .findFirst()
                .get();
    }
    private static final Location getTopRight(Location northMost, Location eastMost)
    {
        return Stream.iterate(northMost,Location::east)
                .filter(location -> !eastMost.isEastOf(location))
                .findFirst()
                .get();
    }
    private static final Location getTopLeft(Location northMost, Location westMost)
    {
        return Stream.iterate(northMost,Location::west)
                .filter(location -> !westMost.isWestOf(location))
                .findFirst()
                .get();
    }

    private static final List<List<Location>> gridWith(List<Location> topRow, Location topLeft, Location bottomLeft)
    {
        int rowCount = topLeft.equals(bottomLeft) ? 1 : topLeft.distanceFrom(bottomLeft) + 1;
        return Stream.iterate(topRow,
                              row -> row.stream().map(Location::south).collect(toList()))
                .limit(rowCount)
                .collect(toList());
    }

    private static final List<Location> topRowWith(Location topLeft, Location topRight)
    {
        int columnCount = topLeft.equals(topRight) ? 1 : topLeft.distanceFrom(topRight) + 1;
        return Stream.iterate(topLeft,Location::east)
                .limit(columnCount)
                .collect(toList());
    }

    // Constants //////////////////////////////////////////////////////////////////////////////////////////////////

    private static final List<Long> NEIGHBOUR_COUNTS_ALLOWING_SURVIVAL = asList(2L,3L);
    private static final String NEWLINE_SUFFIX = System.getProperty("line.separator");
    private static final String NO_PREFIX = "";
    private static final String NO_DELIMITER = "";
}

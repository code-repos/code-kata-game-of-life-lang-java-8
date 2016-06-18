/**
 * Created by Philip Schwarz on 18/06/2016.
 */

import org.junit.Test;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.*;

public class WorldTest
{
    // useful: http://www.squidproco.com/game-of-life/

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private enum Cell { ALIVE, DEAD }

    private static final Cell O = Cell.ALIVE;
    private static final Cell _ = Cell.DEAD;

    @Test
    public void given_world_has_no_live_cells_when_it_is_asked_if_it_is_empty_then_it_answers_it_is_empty() throws Exception
    {
        assertTrue(World.empty().isEmpty());
    }

    @Test
    public void given_world_has_a_live_cell_when_it_is_asked_if_it_is_empty_then_it_answers_it_is_not_empty() throws Exception
    {
        Location location = new Location(5,7);
        World worldWithOneLiveCell = World.withCellsAt(location);

        assertFalse(worldWithOneLiveCell.isEmpty());
    }

    @Test
    public void given_world_has_live_cells_when_it_is_asked_if_it_is_empty_then_it_answers_it_is_not_empty() throws Exception
    {
        Location location = new Location(5,7);
        Location otherLocation = new Location(2,6);
        World worldWithTwoLiveCells = World.withCellsAt(location,otherLocation);

        assertFalse(worldWithTwoLiveCells.isEmpty());
    }

    @Test
    public void given_world_is_empty_when_it_evolves_it_remains_empty() throws Exception
    {
        assertTrue(World.empty().evolve().isEmpty());
    }

    @Test
    public void given_world_contains_live_cell_with_no_neighbours_when_world_evolves_then_cell_dies() throws Exception
    {
        Location location = new Location(3,4);

        assertFalse(World.withCellsAt(location).evolve().hasLiveCellAt(location));
    }

    @Test
    public void given_world_contains_live_cell_with_one_neighbour_when_world_evolves_then_cell_dies() throws Exception
    {
        Location location = new Location(3,4);
        Location neighbouringLocation = new Location(3,5);

        assertFalse(World.withCellsAt(location, neighbouringLocation).evolve().hasLiveCellAt(location));
    }

    @Test
    public void given_world_contains_live_cell_with_two_neighbours_when_world_evolves_then_cell_stays_alive() throws Exception
    {
        Location location = new Location(3,4);
        Location firstNeighbouringLocation = new Location(3,3);
        Location secondNeighbouringLocation = new Location(3,5);

        assertTrue(World.withCellsAt(location, firstNeighbouringLocation, secondNeighbouringLocation).evolve().hasLiveCellAt(location));
    }

    @Test
    public void given_world_contains_live_cell_with_three_neighbours_when_world_evolves_then_cell_stays_alive() throws Exception
    {
        Location topLeft = new Location(3,2);
        Location top = new Location(3,3);
        Location topRight = new Location(3,4);
        Location location = new Location(4,3);

        assertTrue(World.withCellsAt(topLeft, top, topRight, location).evolve().hasLiveCellAt(location));
    }

    @Test
    public void given_world_contains_live_cell_with_four_neighbours_when_world_evolves_then_cell_dies() throws Exception
    {
        List<World> worlds = asList(

            world(new Cell[][]{
                {   _,_,_,_,_   },
                {   _,O,O,O,_   },
                {   _,_,O,O,_   },
                {   _,_,_,_,_   },
                {   _,_,_,_,_   }}),

            world(new Cell[][]{
                {   _,_,_,_,_   },
                {   _,O,O,_,_   },
                {   _,_,O,_,_   },
                {   _,_,O,O,_   },
                {   _,_,_,_,_   },}),

            world(new Cell[][]{
                {   _,_,_,_,_   },
                {   _,O,_,O,_   },
                {   _,_,O,_,_   },
                {   _,O,_,O,_   },
                {   _,_,_,_,_   },}),

            world(new Cell[][]{
                {   _,_,_,_,_   },
                {   _,O,_,_,_   },
                {   _,O,O,O,_   },
                {   _,O,_,_,_   },
                {   _,_,_,_,_   },})
        );

        worlds.forEach( world -> {

            assertFalse(world
                        .evolve()
                        .hasLiveCellAt(location(3,3)));
        });
    }

    @Test
    public void given_world_contains_dead_cell_with_three_neighbours_when_world_evolves_then_cell_comes_alive() throws Exception
    {
        World world = world(new Cell[][]{
        {   _,_,_,_,_   },
        {   _,O,O,O,_   },
        {   _,_,_,_,_   },
        {   _,_,_,_,_   }});

        assertTrue(world
                   .evolve()
                   .hasLiveCellAt(location(3,3)));
    }

    @Test
    public void given_world_contains_block_still_life_pattern_when_world_evolves_then_it_stays_unchanged() throws Exception
    {
        World world = world(new Cell[][]{
        {   _,_,_,_   },
        {   _,O,O,_   },
        {   _,O,O,_   },
        {   _,_,_,_   }});
        assertEquals(world, world.evolve());
    }

    @Test
    public void given_world_contains_beehive_still_life_pattern_when_world_evolves_then_it_stays_unchanged() throws Exception
    {
        World world = world(new Cell[][]{
        {   _,_,_,_,_,_   },
        {   _,_,O,O,_,_   },
        {   _,O,_,_,O,_   },
        {   _,_,O,O,_,_   },
        {   _,_,_,_,_,_   }});
        assertEquals(world, world.evolve());
    }

    @Test
    public void given_world_contains_loaf_still_life_pattern_when_world_evolves_then_it_stays_unchanged() throws Exception
    {
        World world = world(new Cell[][]{
        {   _,_,_,_,_,_   },
        {   _,_,O,O,_,_   },
        {   _,O,_,_,O,_   },
        {   _,_,O,_,O,_   },
        {   _,_,_,O,_,_   },
        {   _,_,_,_,_,_   }});
        assertEquals(world, world.evolve());
    }

    @Test
    public void given_world_contains_boat_still_life_pattern_when_world_evolves_then_it_stays_unchanged() throws Exception
    {
        World world = world(new Cell[][]{
        {   _,_,_,_,_   },
        {   _,O,O,_,_   },
        {   _,O,_,O,_   },
        {   _,_,O,_,_   },
        {   _,_,_,_,_   }});
        assertEquals(world, world.evolve());
    }

    @Test
    public void given_world_contains_blinker_oscillator_when_world_evolves_then_blinker_oscillates() throws Exception
    {
        World world = world(new Cell[][]{
        {   _,_,_,_,_   },
        {   _,_,_,_,_   },
        {   _,O,O,O,_   },
        {   _,_,_,_,_   },
        {   _,_,_,_,_   }});

        World expectedWorld = world(new Cell[][]{
        {   _,_,_,_,_   },
        {   _,_,O,_,_   },
        {   _,_,O,_,_   },
        {   _,_,O,_,_   },
        {   _,_,_,_,_   }});

        assertEquals(expectedWorld, world.evolve());
    }

    @Test
    public void given_world_contains_toad_oscillator_when_world_evolves_then_toad_evolves() throws Exception
    {
        World world = world(new Cell[][]{
        {   _,_,_,_,_,_   },
        {   _,_,_,_,_,_   },
        {   _,_,O,O,O,_   },
        {   _,O,O,O,_,_   },
        {   _,_,_,_,_,_   },
        {   _,_,_,_,_,_   }});

        World expectedWorld = world(new Cell[][]{
        {   _,_,_,_,_,_   },
        {   _,_,_,O,_,_   },
        {   _,O,_,_,O,_   },
        {   _,O,_,_,O,_   },
        {   _,_,O,_,_,_   },
        {   _,_,_,_,_,_   }});

        assertEquals(expectedWorld, world.evolve());
    }

    @Test
    public void given_world_contains_beacon_oscillator_when_world_evolves_then_beacon_evolves() throws Exception
    {
        World world = world(new Cell[][]{
        {   _,_,_,_,_,_   },
        {   _,O,O,_,_,_   },
        {   _,O,_,_,_,_   },
        {   _,_,_,_,O,_   },
        {   _,_,_,O,O,_   },
        {   _,_,_,_,_,_   }});

        World expectedWorld = world(new Cell[][]{
        {   _,_,_,_,_,_   },
        {   _,O,O,_,_,_   },
        {   _,O,O,_,_,_   },
        {   _,_,_,O,O,_   },
        {   _,_,_,O,O,_   },
        {   _,_,_,_,_,_   }});

        assertEquals(expectedWorld, world.evolve());
    }

    @Test
    public void given_world_contains_pulsar_oscillator_when_world_evolves_then_pulsar_evolves() throws Exception {

        World worldIn1stPulsarState = world(new Cell[][]{
        {   _,_,_,_,_,_,_,_,_,_,_,_,_,_,_   },
        {   _,_,_,O,O,O,_,_,_,O,O,O,_,_,_   },
        {   _,_,_,_,_,_,_,_,_,_,_,_,_,_,_   },
        {   _,O,_,_,_,_,O,_,O,_,_,_,_,O,_   },
        {   _,O,_,_,_,_,O,_,O,_,_,_,_,O,_   },
        {   _,O,_,_,_,_,O,_,O,_,_,_,_,O,_   },
        {   _,_,_,O,O,O,_,_,_,O,O,O,_,_,_   },
        {   _,_,_,_,_,_,_,_,_,_,_,_,_,_,_   },
        {   _,_,_,O,O,O,_,_,_,O,O,O,_,_,_   },
        {   _,O,_,_,_,_,O,_,O,_,_,_,_,O,_   },
        {   _,O,_,_,_,_,O,_,O,_,_,_,_,O,_   },
        {   _,O,_,_,_,_,O,_,O,_,_,_,_,O,_   },
        {   _,_,_,_,_,_,_,_,_,_,_,_,_,_,_   },
        {   _,_,_,O,O,O,_,_,_,O,O,O,_,_,_   },
        {   _,_,_,_,_,_,_,_,_,_,_,_,_,_,_   }});

        World worldIn2ndPulsarState = world(new Cell[][]{
        {   _,_,_,_,O,_,_,_,_,_,O,_,_,_,_   },
        {   _,_,_,_,O,_,_,_,_,_,O,_,_,_,_   },
        {   _,_,_,_,O,O,_,_,_,O,O,_,_,_,_   },
        {   _,_,_,_,_,_,_,_,_,_,_,_,_,_,_   },
        {   O,O,O,_,_,O,O,_,O,O,_,_,O,O,O   },
        {   _,_,O,_,O,_,O,_,O,_,O,_,O,_,_   },
        {   _,_,_,_,O,O,_,_,_,O,O,_,_,_,_   },
        {   _,_,_,_,_,_,_,_,_,_,_,_,_,_,_   },
        {   _,_,_,_,O,O,_,_,_,O,O,_,_,_,_   },
        {   _,_,O,_,O,_,O,_,O,_,O,_,O,_,_   },
        {   O,O,O,_,_,O,O,_,O,O,_,_,O,O,O   },
        {   _,_,_,_,_,_,_,_,_,_,_,_,_,_,_   },
        {   _,_,_,_,O,O,_,_,_,O,O,_,_,_,_   },
        {   _,_,_,_,O,_,_,_,_,_,O,_,_,_,_   },
        {   _,_,_,_,O,_,_,_,_,_,O,_,_,_,_   }});

        World worldIn3rdPulsarState = world(new Cell[][]{
        {   _,_,_,_,_,_,_,_,_,_,_,_,_,_,_   },
        {   _,_,_,O,O,_,_,_,_,_,O,O,_,_,_   },
        {   _,_,_,_,O,O,_,_,_,O,O,_,_,_,_   },
        {   _,O,_,_,O,_,O,_,O,_,O,_,_,O,_   },
        {   _,O,O,O,_,O,O,_,O,O,_,O,O,O,_   },
        {   _,_,O,_,O,_,O,_,O,_,O,_,O,_,_   },
        {   _,_,_,O,O,O,_,_,_,O,O,O,_,_,_   },
        {   _,_,_,_,_,_,_,_,_,_,_,_,_,_,_   },
        {   _,_,_,O,O,O,_,_,_,O,O,O,_,_,_   },
        {   _,_,O,_,O,_,O,_,O,_,O,_,O,_,_   },
        {   _,O,O,O,_,O,O,_,O,O,_,O,O,O,_   },
        {   _,O,_,_,O,_,O,_,O,_,O,_,_,O,_   },
        {   _,_,_,_,O,O,_,_,_,O,O,_,_,_,_   },
        {   _,_,_,O,O,_,_,_,_,_,O,O,_,_,_   },
        {   _,_,_,_,_,_,_,_,_,_,_,_,_,_,_   }});

        assertEquals(worldIn2ndPulsarState, worldIn1stPulsarState.evolve());
        assertEquals(worldIn3rdPulsarState, worldIn1stPulsarState.evolve().evolve());
        assertEquals(worldIn1stPulsarState, worldIn1stPulsarState.evolve().evolve().evolve());
    }

    @Test
    public void given_world_contains_just_a_single_cell_when_its_text_representation_is_generated_then_it_is_correct() throws Exception
    {
        World world = World.withCellsAt(new Location(1,1));

        assertEquals("O" + LINE_SEPARATOR, world.toString());
    }

    @Test
    public void given_world_contains_single_cell_when_its_text_representation_is_generated_then_it_is_correct() throws Exception
    {
        World world = world(new Cell[][]{
        {   _,_,O,_,_   }});

        assertEquals("O" + LINE_SEPARATOR, world.toString());
    }

    @Test
    public void given_world_contains_loaf_when_its_text_representation_is_generated_then_it_is_correct() throws Exception
    {
        World world = world(new Cell[][]{
        {   _,_,_,_,_,_   },
        {   _,_,O,O,_,_   },
        {   _,O,_,_,O,_   },
        {   _,_,O,_,O,_   },
        {   _,_,_,O,_,_   },
        {   _,_,_,_,_,_   }});

        assertEquals(
            "_OO_"  + LINE_SEPARATOR +
            "O__O"  + LINE_SEPARATOR +
            "_O_O"  + LINE_SEPARATOR +
            "__O_"  + LINE_SEPARATOR, world.toString());
    }

    @Test
    public void given_world_contains_pulsar_when_its_text_representation_is_generated_then_it_is_correct() throws Exception
    {
        World world = world(new Cell[][]{
        {   _,_,_,_,O,_,_,_,_,_,O,_,_,_,_   },
        {   _,_,_,_,O,_,_,_,_,_,O,_,_,_,_   },
        {   _,_,_,_,O,O,_,_,_,O,O,_,_,_,_   },
        {   _,_,_,_,_,_,_,_,_,_,_,_,_,_,_   },
        {   O,O,O,_,_,O,O,_,O,O,_,_,O,O,O   },
        {   _,_,O,_,O,_,O,_,O,_,O,_,O,_,_   },
        {   _,_,_,_,O,O,_,_,_,O,O,_,_,_,_   },
        {   _,_,_,_,_,_,_,_,_,_,_,_,_,_,_   },
        {   _,_,_,_,O,O,_,_,_,O,O,_,_,_,_   },
        {   _,_,O,_,O,_,O,_,O,_,O,_,O,_,_   },
        {   O,O,O,_,_,O,O,_,O,O,_,_,O,O,O   },
        {   _,_,_,_,_,_,_,_,_,_,_,_,_,_,_   },
        {   _,_,_,_,O,O,_,_,_,O,O,_,_,_,_   },
        {   _,_,_,_,O,_,_,_,_,_,O,_,_,_,_   },
        {   _,_,_,_,O,_,_,_,_,_,O,_,_,_,_   }});

        assertEquals(
            "____O_____O____"     + LINE_SEPARATOR +
            "____O_____O____"     + LINE_SEPARATOR +
            "____OO___OO____"     + LINE_SEPARATOR +
            "_______________"     + LINE_SEPARATOR +
            "OOO__OO_OO__OOO"     + LINE_SEPARATOR +
            "__O_O_O_O_O_O__"     + LINE_SEPARATOR +
            "____OO___OO____"     + LINE_SEPARATOR +
            "_______________"     + LINE_SEPARATOR +
            "____OO___OO____"     + LINE_SEPARATOR +
            "__O_O_O_O_O_O__"     + LINE_SEPARATOR +
            "OOO__OO_OO__OOO"     + LINE_SEPARATOR +
            "_______________"     + LINE_SEPARATOR +
            "____OO___OO____"     + LINE_SEPARATOR +
            "____O_____O____"     + LINE_SEPARATOR +
            "____O_____O____"     + LINE_SEPARATOR
            ,world.toString());
    }

    private static World world(Cell[][] grid){
        return World.withCellsAt(locations(grid));
    }

    private static List<Location> locations(Cell[][] grid)
    {
        int rows = grid.length;
        int cols = grid[0].length;

        return
            rangeClosed(1,rows).boxed().flatMap( row ->
                rangeClosed(1,cols).boxed().flatMap( col ->
                    grid[row-1][col-1] == Cell.ALIVE
                    ? Stream.of(new Location(row,col))
                    : Stream.empty()))
            .collect(toList());

//        rangeClosed(1, rows).boxed().flatMap(row ->
//            rangeClosed(1, cols).boxed().map(col ->
//                    grid[row][col]
//                            ? new Location(row, col)
//                            : null))
//            .filter(Objects::nonNull);
//
//        rangeClosed(1, rows).boxed()
//          .collect(reducing(new LinkedList<>(),
//                            row ->  rangeClosed(1, cols).boxed()
//                                      .collect(reducing(new LinkedList<>(),
//                                                        col -> grid[row][col] ? Collections.singletonList(new Location(row,col)) : Collections.EMPTY_LIST,
//                                                        (List<Location> l, List<Location>r) -> {l.addAll(r); return l; })),
//                            (List<Location> l, List<Location>r) -> {l.addAll(r); return l; }));
//
//        rangeClosed(1, rows).boxed()
//                .collect(reducing(Stream.empty(),
//                        row ->  rangeClosed(1, cols).boxed()
//                                .collect(reducing(Stream.empty(),
//                                        col -> grid[row][col] ? Stream.of(new Location(row,col)) : Stream.empty(),
//                                        (s1,s2)-> Stream.concat(s1,s2))),
//                        (s1,s2)-> Stream.concat(s1,s2)));
    }

    public static Location location(int x, int y){
        return new Location(x, y);
    }
}

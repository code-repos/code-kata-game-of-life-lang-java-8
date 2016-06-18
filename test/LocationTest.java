/**
 * Created by Philip Schwarz on 18/06/2016.
 */

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LocationTest
{
    Location location;

    @Before
    public void setUp() throws Exception
    {
        location = new Location(3,4);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void test_cell_north_east_of_other_cell_is_one_cell_away() throws Exception
    {
        assertEquals(1,location.distanceFrom(location.northEast()));
    }

    @Test
    public void test_cell_north_of_other_cell_is_one_cell_away() throws Exception
    {
        assertEquals(1,location.distanceFrom(location.north()));
    }

    @Test
    public void test_cell_north_west_of_other_cell_is_one_cell_away() throws Exception
    {
        assertEquals(1,location.distanceFrom(location.northWest()));
    }

    @Test
    public void test_cell_west_of_other_cell_is_one_cell_away() throws Exception
    {
        assertEquals(1,location.distanceFrom(location.west()));
    }

    @Test
    public void test_cell_south_west_of_other_cell_is_one_cell_away() throws Exception
    {
        assertEquals(1,location.distanceFrom(location.southWest()));
    }

    @Test
    public void test_cell_south_of_other_cell_is_one_cell_away() throws Exception
    {
        assertEquals(1,location.distanceFrom(location.south()));
    }

    @Test
    public void test_cell_south_east_of_other_cell_is_one_cell_away() throws Exception
    {
        assertEquals(1,location.distanceFrom(location.southEast()));
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void test_cell_two_cells_south_of_other_cell_is_two_cells_away() throws Exception
    {
        assertEquals(2,location.distanceFrom(location.south().south()));
    }

    @Test
    public void test_cell_three_cells_south_of_other_cell_is_three_cells_away() throws Exception
    {
        assertEquals(3,location.distanceFrom(location.south().south().south()));
    }

    @Test
    public void test_cell_three_cells_south_and_two_cells_east_of_other_cell_is_three_cells_away() throws Exception
    {
        assertEquals(3,location.distanceFrom(location.south().south().south().east().east()));
    }

    @Test
    public void test_cell_three_cells_south_and_three_cells_east_of_other_cell_is_three_cells_away() throws Exception
    {
        assertEquals(3,location.distanceFrom(location.south().south().south().east().east().east()));
    }

    @Test
    public void test_cell_three_cells_south_and_five_cells_east_of_other_cell_is_five_cells_away() throws Exception
    {
        assertEquals(5,location.distanceFrom(location.south().south().south().east().east().east().east().east()));
    }
}
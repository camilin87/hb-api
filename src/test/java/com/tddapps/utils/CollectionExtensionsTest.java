package com.tddapps.utils;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static com.tddapps.utils.CollectionExtensions.Intersection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CollectionExtensionsTest {
    @Test
    public void TheIntersectionOfTwoEmptyListsIsEmpty(){
        assertTrue(Intersection(new ArrayList<Integer>(), new ArrayList<>()).isEmpty());
    }

    @Test
    public void TheIntersectionWithAnEmptyListIsEmpty(){
        val l1 = new ArrayList<Integer>(){{
            add(1);
            add(2);
            add(3);
        }};

        assertTrue(Intersection(new ArrayList<>(), l1).isEmpty());
        assertTrue(Intersection(l1, new ArrayList<>()).isEmpty());
    }

    @Test
    public void TheIntersectionReturnsTheCommonElements(){
        val l1 = new ArrayList<Integer>(){{
            add(1);
            add(2);
            add(3);
        }};

        val l2 = new ArrayList<Integer>(){{
            add(10);
            add(2);
            add(3);
            add(4);
            add(5);
        }};

        val expected = new ArrayList<Integer>(){{
            add(2);
            add(3);
        }};

        assertEquals(expected, Intersection(l1, l2));
        assertEquals(expected, Intersection(l2, l1));
    }

    @Test
    public void TheIntersectionIsEmptyWhenThereAreNoCommonElements(){
        val l1 = new ArrayList<Integer>(){{
            add(1);
            add(2);
            add(3);
        }};

        val l2 = new ArrayList<Integer>(){{
            add(4);
            add(5);
        }};

        assertTrue(Intersection(l1, l2).isEmpty());
        assertTrue(Intersection(l2, l1).isEmpty());
    }

}

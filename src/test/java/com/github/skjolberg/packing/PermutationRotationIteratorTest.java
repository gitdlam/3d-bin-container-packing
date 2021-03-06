package com.github.skjolberg.packing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class PermutationRotationIteratorTest {

	@Test
	public void testCount() {
		for(int i = 1; i <= 8; i++) {
			Box container = new Box(3 * (i + 1), 1, 1, 0);
			List<BoxItem> products1 = new ArrayList<BoxItem>();
			
			for(int k = 0; k < i; k++) {
				BoxItem item = new BoxItem(new Box(Integer.toString(k), 3, 1, 1, 0));
				
				products1.add(item);
			}
	
			PermutationRotationIterator rotator = new PermutationRotationIterator(products1, container, true);
			
			long count = rotator.countRotations();
			
			int rotate = 0;
			do {
				rotate++;
			} while(rotator.nextRotation());
			
			assertEquals(count, rotate);
		}
	}
	
	@Test
	public void testNumberOfUnconstrainedRotations() {
		Box container = new Box(3, 3, 3, 0);
		
		List<BoxItem> products = new ArrayList<BoxItem>();
		
		products.add(new BoxItem(new Box("0", 1, 2, 3, 0), 1));

		PermutationRotationIterator rotator = new PermutationRotationIterator(products, container, true);

		assertEquals(6, rotator.countRotations());
	}
	
	@Test
	public void testNumberOfConstrainedRotations() {
		Box container = new Box(1, 2, 3, 0);
		
		List<BoxItem> products = new ArrayList<BoxItem>();
		
		products.add(new BoxItem(new Box("0", 1, 2, 3, 0), 1));

		PermutationRotationIterator rotator = new PermutationRotationIterator(products, container, true);

		assertEquals(1, rotator.countRotations());
	}
	
	@Test
	public void testNumberOfRotationsForSquare2D() {
		Box container = new Box(3, 3, 3, 0);
		
		List<BoxItem> products = new ArrayList<BoxItem>();
		
		products.add(new BoxItem(new Box("0", 3, 1, 1, 0), 1));

		PermutationRotationIterator rotator = new PermutationRotationIterator(products, container, true);

		assertEquals(3, rotator.countRotations());
	}
	
	@Test
	public void testNumberOfConstrainedRotationsForSquare2D() {
		Box container = new Box(3, 1, 1, 0);
		
		List<BoxItem> products = new ArrayList<BoxItem>();
		
		products.add(new BoxItem(new Box("0", 3, 1, 1, 0), 1));

		PermutationRotationIterator rotator = new PermutationRotationIterator(products, container, true);

		assertEquals(1, rotator.countRotations());
	}
	
	@Test
	public void testNumberOfRotationsForSquare3D() {
		Box container = new Box(3, 3, 3, 0);
		
		List<BoxItem> products = new ArrayList<BoxItem>();
		
		products.add(new BoxItem(new Box("0", 1, 1, 1, 0), 1));

		PermutationRotationIterator rotator = new PermutationRotationIterator(products, container, true);

		assertEquals(1, rotator.countRotations());
	}
	
	@Test
	public void testRotation() {
		Box container = new Box(9, 1, 1, 0);
		
		List<BoxItem> products = new ArrayList<BoxItem>();
		
		products.add(new BoxItem(new Box("0", 1, 1, 3, 0), 1));
		products.add(new BoxItem(new Box("1", 1, 1, 3, 0), 1));
		products.add(new BoxItem(new Box("2", 1, 1, 3, 0), 1));

		PermutationRotationIterator rotator = new PermutationRotationIterator(products, container, true);

		assertEquals(1, rotator.countRotations());
		
		do {
			// check order unchanged
			for(int i = 0; i < products.size(); i++) {
				assertEquals(Integer.toString(i), rotator.get(i).getName());
			}

			// all rotations can fit
			for(int i = 0; i < products.size(); i++) {
				assertTrue(rotator.get(i).fitsInside3D(container));
			}
		} while(rotator.nextRotation());
		
	}
	
	@Test
	public void testPermutations() {
		Box container = new Box(9, 1, 1, 0);
		
		List<BoxItem> products = new ArrayList<BoxItem>();
		
		products.add(new BoxItem(new Box("0", 1, 1, 3, 0), 1));
		products.add(new BoxItem(new Box("1", 1, 1, 3, 0), 1));
		products.add(new BoxItem(new Box("2", 1, 1, 3, 0), 1));
		products.add(new BoxItem(new Box("3", 1, 1, 3, 0), 1));
		products.add(new BoxItem(new Box("4", 1, 1, 3, 0), 1));

		PermutationRotationIterator rotator = new PermutationRotationIterator(products, container, true);

		int count = 0;
		do {
			count++;
		} while(rotator.nextPermutation());
		
		assertEquals( 5 * 4 * 3 * 2 * 1, count);
	}
	
	@Test
	public void testPermutationsWithMultipleBoxes() {
		Box container = new Box(9, 1, 1, 0);
		
		List<BoxItem> products = new ArrayList<BoxItem>();
		
		products.add(new BoxItem(new Box("0", 1, 1, 3, 0), 2));
		products.add(new BoxItem(new Box("1", 1, 1, 3, 0), 4));

		PermutationRotationIterator rotator = new PermutationRotationIterator(products, container, true);

		int count = 0;
		do {
			count++;
		} while(rotator.nextPermutation());
		
		assertEquals( (6 * 5 * 4 * 3 * 2 * 1) / ((4 * 3 * 2 * 1) * (2 * 1)), count);
	}	
	
	@Test
	public void testCounts() {
		List<BoxItem> products1 = new ArrayList<BoxItem>();

		products1.add(new BoxItem(new Box(5, 10, 10, 0), 2));
		products1.add(new BoxItem(new Box(5, 10, 10, 0), 2));

		int n = 4;
		
		PermutationRotationIterator rotator = new PermutationRotationIterator(products1, new Box(5 * n, 10, 10, 0), true);

		int length = rotator.length();
		
		assertEquals(4, length);
		
	}
	
	
	@Test
	public void testCountPermutations1() {
		int n = 25;
		
		List<Box> containers = new ArrayList<Box>();
		containers.add(new Box(5 * n, 10, 10, 0));
		
		List<BoxItem> products1 = new ArrayList<BoxItem>();
		for(int k = 0; k < n; k++) {
			products1.add(new BoxItem(new Box(5, 10, 10, 0), 1));
		}

		PermutationRotation[] rotationMatrix = PermutationRotationIterator.toRotationMatrix(products1, true);
		
		PermutationRotationIterator iterator = new PermutationRotationIterator(containers.get(0), rotationMatrix);

		assertEquals(-1L, iterator.countPermutations());
	}
	
	@Test
	public void testCountPermutations2() {
		int n = 25;
		
		List<Box> containers = new ArrayList<Box>();
		containers.add(new Box(5 * n, 10, 10, 0));
		
		List<BoxItem> products1 = new ArrayList<BoxItem>();
		for(int k = 0; k < n; k++) {
			products1.add(new BoxItem(new Box(5, 10, 10, 0), 2));
		}

		PermutationRotation[] rotationMatrix = PermutationRotationIterator.toRotationMatrix(products1, true);
		
		PermutationRotationIterator iterator = new PermutationRotationIterator(containers.get(0), rotationMatrix);

		assertEquals(-1L, iterator.countPermutations());
	}

	@Test
	public void testRemovePermutations1() {
		Box container = new Box(9, 1, 1, 0);
		
		List<BoxItem> products = new ArrayList<BoxItem>();
		
		products.add(new BoxItem(new Box("0", 1, 1, 3, 0), 1));
		products.add(new BoxItem(new Box("1", 1, 1, 3, 0), 1));
		products.add(new BoxItem(new Box("2", 1, 1, 3, 0), 1));
		products.add(new BoxItem(new Box("3", 1, 1, 3, 0), 1));
		products.add(new BoxItem(new Box("4", 1, 1, 3, 0), 1));

		PermutationRotationIterator rotator = new PermutationRotationIterator(products, container, true);

		rotator.removePermutations(3);
		
		int[] permutations = rotator.getPermutations();
		
		assertEquals(3, permutations[0]);
		assertEquals(4, permutations[1]);
	}

	@Test
	public void testRemovePermutations2() {
		Box container = new Box(9, 1, 1, 0);
		
		List<BoxItem> products = new ArrayList<BoxItem>();
		
		products.add(new BoxItem(new Box("0", 1, 1, 3, 0), 1));
		products.add(new BoxItem(new Box("1", 1, 1, 3, 0), 1));
		products.add(new BoxItem(new Box("2", 1, 1, 3, 0), 1));
		products.add(new BoxItem(new Box("3", 1, 1, 3, 0), 1));
		products.add(new BoxItem(new Box("4", 1, 1, 3, 0), 1));

		PermutationRotationIterator rotator = new PermutationRotationIterator(products, container, true);

		List<Integer> remove = new ArrayList<>();
		remove.add(2);
		remove.add(4);
		rotator.removePermutations(remove);
		
		int[] permutations = rotator.getPermutations();
		assertEquals(0, permutations[0]);
		assertEquals(1, permutations[1]);
		assertEquals(3, permutations[2]);
	}
	
}

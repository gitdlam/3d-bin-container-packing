package com.github.skjolberg.packing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * Rotation and permutations built into the same class. Minimizes the number of
 * rotations. <br>
 * <br>
 * The maximum number of combinations is n! * 6^n, however after accounting for
 * bounds and sides with equal lengths the number can be a lot lower (and this
 * number can be obtained before starting the calculation). <br>
 * <br>
 * Assumes a do-while approach:
 * 
 * <pre>{@code 
 * do {
 * 	do {
 * 		for (int i = 0; i < n; i++) {
 * 			Box box = instance.get(i);
 * 			// .. your code here
 * 		}
 * 	} while (instance.nextRotation());
 * } while (instance.nextPermutation());
 * 
 * }</pre>
 * 
 * @see <a href=
 *      "https://www.nayuki.io/page/next-lexicographical-permutation-algorithm"
 *      target="_top">next-lexicographical-permutation-algorithm</a>
 */

public class PermutationRotationIterator {
	
	public static PermutationRotation[] toRotationMatrix(List<BoxItem> list, boolean rotate3D) {
		PermutationRotation[] boxes = new PermutationRotation[list.size()];
		for(int i = 0; i < list.size(); i++) {
			Box box = list.get(i).getBox();
			
			// make sure not to rotate the original box, so that it stays in its original orentation.
			
			List<Box> result = new ArrayList<>();
			if(rotate3D) {
				Box box0 = box.clone();
				boolean square0 = box.isSquare2D();
				
				result.add(box0);
				
				if(!box.isSquare3D()) {
					
					box = box.clone().rotate3D();
					boolean square1 = box.isSquare2D();
					
					result.add(box);
	
					box = box.clone().rotate3D();
					boolean square2 = box.isSquare2D();
	
					result.add(box);
	
					if(!square0 && !square1 && !square2) {
						box = box.clone().rotate2D3D();
						result.add(box);
						
						box = box.clone().rotate3D();
						result.add(box);
		
						box = box.clone().rotate3D();
						result.add(box);
					}
				}
			} else {
				result.add(box.clone());
				
				// do not rotate 2d if square
				if(!box.isSquare2D()) {
					result.add(box.clone().rotate2D());
				}
			}

			boxes[i] = new PermutationRotation(list.get(i).getCount(), result.toArray(new Box[result.size()]));
		}
		return boxes;
	}
	
	protected final PermutationRotation[] matrix;
	protected final Dimension dimension;
	protected int[] reset;
	protected int[] rotations; // 2^n or 6^n
	protected int[] permutations; // n!

	public PermutationRotationIterator(List<BoxItem> list, Dimension bound, boolean rotate3D) {
		this(bound, toRotationMatrix(list, rotate3D));
	}

	public PermutationRotationIterator(Dimension bound, PermutationRotation[] unconstrained) {
		List<Integer> types = new ArrayList<>(unconstrained.length * 2);

		List<PermutationRotation> matrix = new ArrayList<>(unconstrained.length);
		for(int i = 0; i < unconstrained.length; i++) {
			List<Box> result = new ArrayList<>();
			
			Box[] boxes = unconstrained[i].getBoxes();
			for(int k = 0; k < boxes.length; k++) {
				if(boxes[k] != null && boxes[k].fitsInside3D(bound)) {
					result.add(boxes[k]);
				}
			}
			
			// create PermutationRotation even if empty, so that permutations are directly 
			// comparable between parallel instances of this class
			matrix.add(new PermutationRotation(unconstrained[i].getCount(), result.toArray(new Box[result.size()])));

			if(!result.isEmpty()) {
				for(int k = 0; k < unconstrained[i].getCount(); k++) {
					types.add(i);
				}
			}
		}

		this.matrix = matrix.toArray(new PermutationRotation[matrix.size()]);

		this.dimension = bound;
		
		// permutations is a 'pointer' list
		// keep the the number of permutations tight; 
		// identical boxes need not be interchanged
		permutations = new int[types.size()];
		
		for(int i = 0; i < permutations.length; i++) {
			permutations[i] = types.get(i);
		}
		
		reset = new int[permutations.length];
		rotations = new int[permutations.length];
		System.arraycopy(reset, 0, rotations, 0, rotations.length);
	}
	
	public void removePermutations(int count) {
		// discard a number of items
		int newLength = permutations.length - count;
		
		int[] permutations = new int[this.permutations.length - count];
		System.arraycopy(this.permutations, count, permutations, 0, newLength);

		this.rotations = new int[permutations.length];
		this.reset = new int[permutations.length];
		this.permutations = permutations;
		
		Arrays.sort(permutations); // ascending order to make the permutation logic work
	}
	
	public void removePermutations(List<Integer> removed) {
		
		int[] permutations = new int[this.permutations.length - removed.size()];
		
		int index = 0;
		permutations:
		for (int j : this.permutations) {
			for (int i = 0; i < removed.size(); i++) {
				if(removed.get(i).intValue() == j) {
					// skip this
					removed.remove(i);
					
					continue permutations;
				}
			}
			
			permutations[index] = j;
			
			index++;
		}
		
		this.rotations = new int[permutations.length];
		this.reset = new int[permutations.length];
		this.permutations = permutations;
		Arrays.sort(permutations); // ascending order to make the permutation logic work
	}
	
	/**
	 * 
	 * Check how many orientations (i.e. rotations) the box fits
	 * 
	 * @param index rotation for permutation at index
	 * @return number of rotations (or zero if none fits)
	 */
	
	public int getOrientations(int index) {
		return matrix[permutations[index]].getBoxes().length;
	}
	
	public boolean nextRotation() {
		// next rotation
		for(int i = 0; i < rotations.length; i++) {
			while(rotations[i] < matrix[permutations[i]].getBoxes().length - 1) {
				rotations[i]++;
				
				// reset all previous counters 
				System.arraycopy(reset, 0, rotations, 0, i);
				
				return true;
			}
		}
		
		return false;
	}
	
	public int[] getRotations() {
		return rotations;
	}
	
	public int[] getPermutations() {
		return permutations;
	}
	
	public boolean isWithinHeight(int fromIndex, int height) {
		for(int i = fromIndex; i < permutations.length; i++) {
			if(get(i).getHeight() > height) {
				return false;
			}
		}
		return true;
	}
	
	protected void resetRotations() {
		System.arraycopy(reset, 0, rotations, 0, rotations.length);
	}
	
	public long countRotations() {
		long n = 1;
		for(int i = 0; i < rotations.length; i++) {
			if(Long.MAX_VALUE / matrix[rotations[i]].getBoxes().length <= n) {
				return -1L;
			}

			n = n * matrix[rotations[i]].getBoxes().length;
		}
		return n;
	}
	
	public long countPermutations() {
		// reduce permutations for boxes which are duplicated
		
		int maxCount = 0;
		for(int i = 0; i < matrix.length; i++) {
			if(maxCount < matrix[i].getCount()) {
				maxCount = matrix[i].getCount();
			}
		}

		long n = 1;
		if(maxCount > 1) {
			int[] factors = new int[maxCount];
			for(int i = 0; i < matrix.length; i++) {
				for(int k = 0; k < matrix[i].getCount(); k++) {
					factors[k]++;
				}
			}

			for(long i = 0; i < permutations.length; i++) {
				if(Long.MAX_VALUE / (i + 1) <= n) {
					return -1L;
				}
				
				n = n * (i + 1);
				
				for(int k = 1; k < maxCount; k++) {
					while(factors[k] > 0 && n % (k + 1) == 0) {
						n = n / (k + 1);
						
						factors[k]--;
					}
				}
			}
			
			for(int k = 1; k < maxCount; k++) {
				while(factors[k] > 0) {
					n = n / (k + 1);
					
					factors[k]--;
				}
			}
		} else {
			for(long i = 0; i < permutations.length; i++) {
				if(Long.MAX_VALUE / (i + 1) <= n) {
					return -1L;
				}
				n = n * (i + 1);
			}
		}
		return n;
	}

	
	public Box get(int index) {
		return matrix[permutations[index]].getBoxes()[rotations[index]];
	}
	
	public boolean nextPermutation() {
		resetRotations();
		
	    // Find longest non-increasing suffix
		
	    int i = permutations.length - 1;
	    while (i > 0 && permutations[i - 1] >= permutations[i])
	        i--;
	    // Now i is the head index of the suffix
	    
	    // Are we at the last permutation already?
	    if (i <= 0) {
	        return false;
	    }
	    
	    // Let array[i - 1] be the pivot
	    // Find rightmost element that exceeds the pivot
	    int j = permutations.length - 1;
	    while (permutations[j] <= permutations[i - 1])
	        j--;
	    // Now the value array[j] will become the new pivot
	    // Assertion: j >= i
	    
	    // Swap the pivot with j
	    int temp = permutations[i - 1];
	    permutations[i - 1] = permutations[j];
	    permutations[j] = temp;
	    
	    // Reverse the suffix
	    j = permutations.length - 1;
	    while (i < j) {
	        temp = permutations[i];
	        permutations[i] = permutations[j];
	        permutations[j] = temp;
	        i++;
	        j--;
	    }
	    
	    // Successfully computed the next permutation
	    return true;
	}
	
	public int length() {
		return permutations.length;
	}

	public PermutationRotationState getState() {
		return new PermutationRotationState(rotations, permutations);
	}
	
	public void setState(PermutationRotationState state) {
		this.rotations = state.getRotations();
		this.permutations = state.getPermutations();
	}
	
	/**
	 * Get number of box items within the constraints.
	 * 
	 * @return number between 0 and number of {@linkplain BoxItem}s used in the constructor.
	 */
	
	public int boxItemLength() {
		return matrix.length;
	}
	
	public Dimension getDimension() {
		return dimension;
	}
}

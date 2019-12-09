package thinclab.legacy;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;

class Pair {
	
		private DD dd1;
		private DD dd2;
		
		private int hash;

		public Pair(DD dd1, DD dd2) {
			
			this.dd1 = dd1;
			this.dd2 = dd2;
			
			this.precomputeHash();
		}
		
		private void precomputeHash() {
			/*
			 * Compute hash after object init to avoid computing it repeatedly
			 */
			this.hash = 
					new HashCodeBuilder()
						.append(this.dd1.hashCode())
						.append(this.dd2.hashCode())
						.toHashCode();
		}

		@Override
		public int hashCode() {
			/*
			 * Over ride hashcode implementation
			 */
//			HashCodeBuilder builder = new HashCodeBuilder();
//			builder.append(dd1.hashCode());
//			builder.append(dd2.hashCode());
//			
//			return builder.toHashCode();
			
			return this.hash;
		}

		@Override
		public boolean equals(Object obj) {
				
				if (obj.getClass() != getClass()) return false;
				
				Pair pair = (Pair)obj;

				return new EqualsBuilder()
						.append(this.dd1, pair.dd1)
						.append(this.dd2, pair.dd2)
						.isEquals();
		}
}

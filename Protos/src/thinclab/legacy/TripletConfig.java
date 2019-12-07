package thinclab.legacy;

import org.apache.commons.lang3.builder.HashCodeBuilder;

class TripletConfig {
		private DD dd1;
		private DD dd2;
		private int[][] config;

		private int hash;
		
		public TripletConfig(DD dd1, DD dd2, int[][] config) {
				this.dd1 = dd1;
				this.dd2 = dd2;
				this.config = config; // Config.clone(config);
				
				this.precomputeHash();
		}
		
		private void precomputeHash() {
			/*
			 * Compute hash after init to avoid computing it at every call to hashCode
			 */
			
			this.hash = 
					new HashCodeBuilder()
						.append(this.dd1.hashCode())
						.append(this.dd2.hashCode())
						.append(Config.hashCode(this.config))
						.toHashCode();
		}

		public int hashCode() {
//				return dd1.getAddress() + dd2.getAddress() + Config.hashCode(config);
			return this.hash;
		}

		public boolean equals(Object obj) {
				
				if (obj.getClass() != getClass()) return false;
				TripletConfig triplet = (TripletConfig)obj;

				if (((dd1.equals(triplet.dd1) && dd2.equals(triplet.dd2)) || 
						 (dd2.equals(triplet.dd1) && dd1.equals(triplet.dd2))) && 
						Config.equals(config, triplet.config)) 
						return true;
				else return false;
		}
}

package victor.clean.lambdas;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.function.BiFunction;

import victor.clean.lambdas.Movie.Type;

class Movie {
	enum Type {
		REGULAR(PriceService::computeRegularPrice), 
		NEW_RELEASE(PriceService::computeNewReleasePrice), 
		CHILDREN(PriceService::computeChildrenPrice);
		public final BiFunction<PriceService, Integer, Integer> priceAlgo;

		private Type(BiFunction<PriceService, Integer, Integer> priceAlgo) {
			this.priceAlgo = priceAlgo;
		}
	}

	private final Type type;

	public Movie(Type type) {
		this.type = type;
	}

}

interface FactorRepo {
	Double getFactor();
}

class PriceService {
	private final FactorRepo repo;
	

	public PriceService(FactorRepo repo) {
		this.repo = repo;
	}

	protected Integer computeNewReleasePrice(int days) {
		return (int) (repo.getFactor() * days);
	}

	protected Integer computeRegularPrice(int days) {
		return days + 1;
	}

	protected Integer computeChildrenPrice(int days) {
		return 5;
	}
	
	public Integer computePrice(Movie.Type type, int days ) {
		return type.priceAlgo.apply(this, days);
	}

}

public class E__TypeSpecific_Functionality {
	public static void main(String[] args) {
		FactorRepo repo = mock(FactorRepo.class);
		when(repo.getFactor()).thenReturn(2d);
		PriceService priceService = new PriceService(repo);
		System.out.println(priceService.computePrice(Type.REGULAR, 2));
		System.out.println(priceService.computePrice(Type.NEW_RELEASE, 2));
		System.out.println(priceService.computePrice(Type.CHILDREN, 2));
		System.out.println("COMMIT now!");
	}
}
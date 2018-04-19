package victor.clean.lambdas;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Stream;

import lombok.Data;

// get the products frequently ordered during the past year

class ProductService {
	private ProductRepo productRepo;

	public List<Product> getFrequentOrderedProducts(List<Order> orders) {
		List<Long> hiddenProductIds = productRepo.getHiddenProductIds();
		Predicate<Product> productIsNotHidden = p-> !hiddenProductIds.contains(p.getId());
		return getFrequentProductsOverTheLastYear(orders)
				.filter(Product::isNotDeleted)
				.filter(productIsNotHidden)
				.collect(toList());
	}

	private Stream<Product> getFrequentProductsOverTheLastYear(List<Order> orders) {
		return getProductCounts(orders).entrySet().stream()
				.filter(e -> e.getValue() >= 10)
				.map(Entry::getKey);
	}

	private Map<Product, Integer> getProductCounts(List<Order> orders) {
		return orders.stream()
				.filter(this::orderWasCreatedDuringThePreviousYear)
				.flatMap(o -> o.getOrderLines().stream())
				.collect(groupingBy(OrderLine::getProduct, summingInt(OrderLine::getItemCount)));
	}

	private boolean orderWasCreatedDuringThePreviousYear(Order o) {
		return o.getCreationDate().isAfter(LocalDate.now().minusYears(1));
	}
}







//VVVVVVVVV ==== supporting (dummy) code ==== VVVVVVVVV
@Data
class Order {
	private Long id;
	private List<OrderLine> orderLines;
	private LocalDate creationDate;
}

@Data
class OrderLine {
	private Product product;
	private int itemCount;
}

@Data
class Product {
	public boolean isNotDeleted() {
		return !deleted;
	}
	private Long id;
	private boolean deleted;
}

interface ProductRepo {
	List<Long> getHiddenProductIds();
}

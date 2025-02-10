package vttp2023.batch4.paf.assessment.repositories;

import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import vttp2023.batch4.paf.assessment.Utils;
import vttp2023.batch4.paf.assessment.models.Accommodation;
import vttp2023.batch4.paf.assessment.models.AccommodationSummary;

@Repository
public class ListingsRepository {

	@Autowired
	private MongoTemplate template;

	// db.listings.aggregate([
	// 	{
	// 		$match: {
	// 			"address.suburb": { $ne: null, $ne: "" } 
	// 		}
	// 	},
	// 	{
	// 		$group: {
	// 			_id: "$address.suburb"
	// 		}
	// 	}
	// ])
	//
	// USING:
	// db.listings.distinct("address.suburb", {
	// 	"address.suburb": { $ne: "", $ne: null }
	// });
	public List<String> getSuburbs(String country) {

		Criteria criteria = Criteria.where("address.suburb").ne("").ne(null);
		Query query = new Query(criteria);

		return template.findDistinct(query, "address.suburb", "listings", String.class);
	}


	// db.listings.find(
	// 	{ 
	// 		"address.suburb": { $regex: "^Monterey$", $options: "i" },
	// 		"price": { $lte: 50 },
	// 		"accommodates": { $gte: 1 },
	// 		"min_nights": { $gte: 1},
	// 	}
	// )
	// .sort({ "price" : -1})
	// .projection({ "name": 1, "accommodates": 1, "price": 1})
	public List<AccommodationSummary> findListings(String suburb, int persons, int duration, float priceRange) {
		
		Criteria criteria = Criteria.where("address.suburb").regex(suburb, "i")
									.and("price").lte(priceRange)
									.and("accommodates").gte(persons)
									.and("min_nights").gte(duration);
		
		MatchOperation matchCritera = Aggregation.match(criteria);

		ProjectionOperation projectFields = Aggregation
			.project("name", "accommodates", "price");

		SortOperation sortByPrice = Aggregation.sort(Sort.Direction.DESC, "price");

		Aggregation pipeline = Aggregation.newAggregation(matchCritera, projectFields, sortByPrice);

		List<Document> documents = template.aggregate(pipeline, "listings", Document.class).getMappedResults();

		List<AccommodationSummary> accomodationSummaries = documents.stream()
			.map( d -> {
				AccommodationSummary accommodationSummary = new AccommodationSummary();
					accommodationSummary.setId(d.getString("_id"));
					accommodationSummary.setName(d.getString("name"));
					accommodationSummary.setAccomodates(d.getInteger("accommodates"));
					accommodationSummary.setPrice(d.get("price", Number.class).floatValue());
				return accommodationSummary;
			}).toList();

		return accomodationSummaries;
	}







	// IMPORTANT: DO NOT MODIFY THIS METHOD UNLESS REQUESTED TO DO SO
	// If this method is changed, any assessment task relying on this method will
	// not be marked
	public Optional<Accommodation> findAccommodatationById(String id) {
		Criteria criteria = Criteria.where("_id").is(id);
		Query query = Query.query(criteria);

		List<Document> result = template.find(query, Document.class, "listings");
		if (result.size() <= 0)
			return Optional.empty();

		return Optional.of(Utils.toAccommodation(result.getFirst()));
	}

}

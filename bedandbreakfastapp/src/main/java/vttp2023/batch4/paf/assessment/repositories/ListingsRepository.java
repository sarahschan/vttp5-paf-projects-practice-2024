package vttp2023.batch4.paf.assessment.repositories;

import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
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
	// db.listings.distinct( "address.suburb" , { "address.suburb" : { $nin : ["", null] } });
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

	/*
	 * Write the native MongoDB query that you will be using for this method
	 * inside this comment block
	 * eg. db.bffs.find({ name: 'fred }) 
	 *
	 *
	 */
	public List<AccommodationSummary> findListings(String suburb, int persons, int duration, float priceRange) {
		return null;
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

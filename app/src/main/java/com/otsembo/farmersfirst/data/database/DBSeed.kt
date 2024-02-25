package com.otsembo.farmersfirst.data.database

import android.database.sqlite.SQLiteDatabase
import com.otsembo.farmersfirst.data.database.dao.ProductDao
import com.otsembo.farmersfirst.data.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

object DBSeed {

    val productList = listOf(
        Product(
            id = 1,
            name = "Fertilizer",
            description = "Premium quality fertilizer enriched with essential nutrients to promote healthy growth and maximize crop yield. Suitable for all types of crops and soil conditions.",
            stock = 100,
            price = 25.99f,
            image = "https://m.media-amazon.com/images/I/713+fXLmLpL.jpg"
        ),
        Product(
            id = 2,
            name = "Pesticide",
            description = "Effective pesticide formulated to control a wide range of pests and insects while ensuring the safety of crops. Provides long-lasting protection for optimal crop production.",
            stock = 80,
            price = 15.49f,
            image = "https://c8.alamy.com/comp/PA28RB/orlando-floridaace-hardwarepesticides-insecticides-poisons-insect-spraysweed-killerroundupshelves-display-saleinterior-insidefl171029120-PA28RB.jpg"
        ),
        Product(
            id = 3,
            name = "Seeds",
            description = "High-quality seeds sourced from trusted suppliers, selected for superior germination rates and disease resistance. Ideal for starting a successful crop planting season.",
            stock = 200,
            price = 8.99f,
            image = "https://northernseeds.ca/cdn/shop/files/seed_packets.jpg?v=1668690089"
        ),
        Product(
            id = 4,
            name = "Irrigation System",
            description = "Advanced irrigation system designed to deliver precise and efficient watering for crops. Features customizable settings and durable construction for long-term use.",
            stock = 30,
            price = 299.99f,
            image = "https://www.watercalculator.org/wp-content/uploads/2017/04/iStock_000010933844_1950.jpg"
        ),
        Product(
            id = 5,
            name = "Tractor",
            description = "Powerful tractor equipped with cutting-edge technology for various farm operations. Offers unmatched performance, reliability, and versatility in agricultural tasks.",
            stock = 10,
            price = 15000f,
            image = "https://www.profi.co.uk/wp-content/uploads/sites/8/2022/01/8a._jd_7r330_ap.jpg"
        ),
        Product(
            id = 6,
            name = "Herbicide",
            description = "Herbicide specially formulated to control weeds effectively without harming crops or the environment. Provides targeted action for weed management in fields and gardens.",
            stock = 50,
            price = 18.75f,
            image = "https://www.agriplexindia.com/cdn/shop/collections/Herbicides.png?crop=center&height=500&v=1673857391&width=600"
        ),
        Product(
            id = 7,
            name = "Mulch",
            description = "Organic mulch made from natural materials to improve soil health and moisture retention. Helps suppress weeds, regulate soil temperature, and promote healthy plant growth.",
            stock = 120,
            price = 5.99f,
            image = "https://www.thespruce.com/thmb/GNryY_TSnuf_2aQ04mODi7yI58w=/4832x0/filters:no_upscale():max_bytes(150000):strip_icc()/SPR-is-dyed-mulch-safe-to-use-2131983-hero-0aef1a51041046e99d27a83b65c19f88.jpg"
        ),
        Product(
            id = 8,
            name = "Fencing",
            description = "Durable fencing solution designed to protect crops and livestock from unwanted intruders. Constructed from high-quality materials for strength, durability, and longevity.",
            stock = 40,
            price = 199.99f,
            image = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ-Qjgj92EQ30TwzCMCGN1fbCkBXTAySv80fA&usqp=CAU"
        ),
        Product(
            id = 9,
            name = "Solar Panels",
            description = "Solar panels harnessing renewable energy from the sun to power farm operations. Provides clean, sustainable energy for reducing electricity costs and environmental impact.",
            stock = 15,
            price = 999.99f,
            image = "https://cdn.britannica.com/91/222691-050-E8BDF226/installing-solar-panels.jpg"
        ),
        Product(
            id = 10,
            name = "Greenhouse Kit",
            description = "Complete greenhouse kit for creating a controlled environment for optimal plant growth. Features include adjustable ventilation, sturdy frame, and UV-resistant covering.",
            stock = 5,
            price = 4999.99f,
            image = "https://growerssolution.com/cdn/shop/files/Untitleddesign-105_1200x.png?v=1698764945"
        ),
        Product(
            id = 11,
            name = "Livestock Feed",
            description = "Nutritious feed formulated to meet the dietary needs of livestock for growth, health, and productivity. Contains essential vitamins, minerals, and proteins for balanced nutrition.",
            stock = 150,
            price = 12.49f,
            image = "https://www.partnersinfoodsolutions.com/sites/default/files/styles/blog_post/public/blog-images/IMG_6231.jpg?itok=S1B-mZGw"
        ),
        Product(
            id = 12,
            name = "Crop Protection Net",
            description = "Durable netting designed to protect crops from birds, pests, and harsh weather conditions. Provides reliable protection while allowing air circulation and sunlight penetration.",
            stock = 60,
            price = 29.99f,
            image = "https://m.media-amazon.com/images/I/61YeSepWIsL._AC_UF1000,1000_QL80_.jpg"
        ),
        Product(
            id = 13,
            name = "Soil Moisture Meter",
            description = "Precision soil moisture meter for accurate measurement of moisture levels in the soil. Helps optimize irrigation scheduling and prevent under or over-watering of crops.",
            stock = 25,
            price = 49.99f,
            image = "https://cdn11.bigcommerce.com/s-625n27otji/images/stencil/1280x1280/products/1644/2660/ph-moisture-meter-1__41741.1639395104.jpg?c=1"
        ),
        Product(
            id = 14,
            name = "Weather Station",
            description = "Advanced weather monitoring station for tracking temperature, humidity, rainfall, wind speed, and other meteorological parameters. Provides real-time data for informed farming decisions.",
            stock = 8,
            price = 299.99f,
            image = "https://store.wildernesslabs.co/cdn/shop/products/IMG_3586_1024x1024@2x.jpg?v=1684531864"
        ),
        Product(
            id = 15,
            name = "Drip Irrigation Kit",
            description = "Efficient drip irrigation kit for delivering water directly to the root zone of plants. Reduces water wastage, promotes healthier plants, and conserves water resources.",
            stock = 20,
            price = 99.99f,
            image = "https://m.media-amazon.com/images/I/81WA-RhHvcL._SL1500_.jpg"
        ),
        Product(
            id = 16,
            name = "Hand Tools Set",
            description = "Comprehensive set of hand tools for various farming tasks including digging, planting, pruning, and weeding. Made from durable materials for long-lasting performance.",
            stock = 100,
            price = 79.99f,
            image = "https://pictures-kenya.jijistatic.com/6868812_s-l1600-1_620x620.jpg"
        ),
        Product(
            id = 17,
            name = "Planting Trays",
            description = "Sturdy planting trays designed for seed starting and propagation. Features multiple cells for organizing seeds and promoting healthy root development in young plants.",
            stock = 80,
            price = 6.99f,
            image = "https://makimara.co.ke/wp-content/uploads/2023/02/seedling-trays-in-kenya.jpg"
        ),
        Product(
            id = 18,
            name = "Agricultural Sprayer",
            description = "Versatile agricultural sprayer for applying pesticides, fertilizers, and herbicides to crops. Offers adjustable spray patterns, ergonomic design, and efficient operation.",
            stock = 35,
            price = 129.99f,
            image = "https://www.deere.co.uk/assets/images/region-2/products/sprayers/john-deere-self-propelled-sprayer-wheat.jpg"
        ),
        Product(
            id = 19,
            name = "Grow Lights",
            description = "Energy-efficient LED grow lights for indoor farming and greenhouse cultivation. Mimics natural sunlight to promote photosynthesis and healthy growth of plants.",
            stock = 10,
            price = 149.99f,
            image = "https://assets.wfcdn.com/im/70729900/compr-r85/1752/175214516/yescom-grow-light.jpg"
        ),
        Product(
            id = 20,
            name = "Compost Bin",
            description = "Durable compost bin for recycling organic waste into nutrient-rich compost for soil improvement. Features easy-to-use design and proper aeration for efficient composting. Ideal for sustainable waste management and enhancing soil fertility.",
            stock = 25,
            price = 39.99f,
            image = "https://www.planetnatural.com/wp-content/uploads/2023/01/compost-tumbler.jpg"
        )
    )

}


interface IFarmersDBSeed {
    suspend fun addProducts()
}

class FarmersDBSeed(
    dbHelper: AppDatabaseHelper,
    private val dao: ProductDao
): IFarmersDBSeed {

    init {
        dbHelper.refresh(dbHelper.writableDatabase)
    }
    override suspend fun addProducts() {
        DBSeed.productList.map { dao.create(it) }.forEach { it.collect() }
    }
}



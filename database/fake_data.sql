--- INTAKES
INSERT INTO intake_profiles (
    name, gender, age_range, activity, kcal, protein, carbohydrates, sugars, fat, saturated_fat,
    fiber, salt, cholesterol, calcium, iron, vitamin_c, vitamin_d, vitamin_b12, potassium, magnesium
) VALUES (
    'Hombre joven activo', 'male', '19-30', 'active',
    3000, 56, 375, 50, 97, 22,
    30, 5, 300, 1000, 8,
    90, 15, 2.4, 3400, 400
);

INSERT INTO intake_profiles (
    name, gender, age_range, activity, kcal, protein, carbohydrates, sugars, fat, saturated_fat,
    fiber, salt, cholesterol, calcium, iron, vitamin_c, vitamin_d, vitamin_b12, potassium, magnesium
) VALUES (
    'Hombre adulto moderado', 'male', '31-50', 'moderate',
    2600, 56, 325, 50, 87, 20,
    30, 5, 300, 1000, 8,
    90, 15, 2.4, 3400, 420
);

INSERT INTO intake_profiles (
    name, gender, age_range, activity, kcal, protein, carbohydrates, sugars, fat, saturated_fat,
    fiber, salt, cholesterol, calcium, iron, vitamin_c, vitamin_d, vitamin_b12, potassium, magnesium
) VALUES (
    'Mujer joven moderada', 'female', '19-30', 'moderate',
    2000, 46, 275, 50, 67, 20,
    25, 5, 300, 1000, 18,
    75, 15, 2.4, 2600, 310
);

INSERT INTO intake_profiles (
    name, gender, age_range, activity, kcal, protein, carbohydrates, sugars, fat, saturated_fat,
    fiber, salt, cholesterol, calcium, iron, vitamin_c, vitamin_d, vitamin_b12, potassium, magnesium
) VALUES (
    'Mujer adulta ligera', 'female', '31-50', 'light',
    1800, 46, 250, 50, 60, 18,
    25, 5, 300, 1000, 18,
    75, 15, 2.4, 2600, 320
);


--- USERS
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Brian Williams', 'qgreen@example.net', '36bd49d171640ae0291b15b8ca6965a0e1a7d36af06440f13237e3e819d20c12', 'consumer', 'Wade and Sons',
        -25.617142, 130.642281, '{"eco_only": false, "vegan": false}', 2
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Gloria Morris', 'davidpatel@example.com', '11d104981dbb65673087b1496f9d76104efb8afc03bc90c9ae66e806817a7e97', 'consumer', 'Thomas Ltd',
        63.7584045, 148.628783, '{"eco_only": false, "vegan": false}', 1
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Edward Foster', 'annamorris@example.org', 'de0c306e7553bef9f023ad797fba419513bc488a1472031a2d0c99221f9d712a', 'restaurant', 'Rojas, Hamilton and Moran',
        -28.3095895, -37.697145, '{"eco_only": false, "vegan": false}', 2
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Christine Grant', 'kgarcia@example.org', '306d8ccb9ac327abd0f6cc5d5e810964dfd2c6ce7580b2e4719febda328de1f6', 'consumer', 'Johnson Inc',
        -16.9368335, -21.530162, '{"eco_only": true, "vegan": false}', 1
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Timothy Price', 'orodriguez@example.org', '19c7282734f930c394553ffea31ab358ff6666c5622906c48560a9451b3a2d41', 'retailer', 'White Ltd',
        -13.1344865, 1.891491, '{"eco_only": false, "vegan": false}', 3
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Ryan Blair', 'tammy34@example.org', 'ccb52b0250cb3a9021b91e49bedf7965bd379abe8dfaac2d2befec5c173959d8', 'retailer', 'Hudson, Elliott and Yates',
        -74.8035995, 152.643695, '{"eco_only": false, "vegan": true}', 1
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Barbara Hatfield', 'david33@example.net', 'ef3932e18b9e1b9c3d9916c72e52e4c41993b8abb520b2a0f647bb0bebba89ea', 'restaurant', 'Lyons Inc',
        79.4540725, -165.383582, '{"eco_only": true, "vegan": false}', 2
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Angela Silva', 'heather24@example.net', '060be3c9cf03274ea5edbbd6031f9e9f581c3f3359c1825a02331a09133f3382', 'consumer', 'White-Roberts',
        -83.7987895, -146.568768, '{"eco_only": true, "vegan": false}', 2
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Tara Little', 'jennifer24@example.com', '3713756203cf61d1c0d8280dc0656679ec3b3eabd95e0d610482d8507d7bfe68', 'restaurant', 'Chavez, Wilson and Rogers',
        84.7339055, 67.874646, '{"eco_only": false, "vegan": false}', 2
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Kathleen Woods', 'michelle98@example.org', '95db4151b9f44296d4fff95a7c2320840ea4b6d68038728bd2e1edc0a85ff873', 'retailer', 'Johnson LLC',
        79.8471265, -68.999880, '{"eco_only": true, "vegan": false}', 2
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Mary Pugh', 'larryestrada@example.com', '76b11dc83185ea7c96bc10725bbe7906d482127f395f53124dad078bb1e49b9f', 'retailer', 'Campbell-Clark',
        -67.073796, 172.055512, '{"eco_only": true, "vegan": false}', 3
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Daniel Garcia', 'michael38@example.org', 'abff1ed7aec3912345ff38cf46a24f97816f342ea110c19182e40fa6da622e7c', 'farmer', 'Lang Group',
        5.4244335, -77.581276, '{"eco_only": false, "vegan": false}', 3
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Susan Sellers', 'juliahopkins@example.com', 'a9e69a390f1b5b6bf9446c73a4ef46e3e65fd83897d0376149e38f528e4c8c83', 'farmer', 'Parker and Sons',
        -49.4439565, 126.957886, '{"eco_only": false, "vegan": false}', 3
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'David Walsh', 'thenson@example.com', '5b1901117b427424c05af52e32b34b7369db5284c473998bf19f720be9bcbe76', 'farmer', 'Smith-Peters',
        14.030108, -32.072871, '{"eco_only": true, "vegan": true}', 2
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Mary Herrera', 'christiankayla@example.net', '05a3100379e9a01f556b8f955c85893618b0a60dee12ecd829f57c97c997d2f1', 'retailer', 'Moore Group',
        27.504756, 50.563676, '{"eco_only": false, "vegan": true}', 3
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Matthew Floyd', 'joshuamadden@example.net', '8d83e108d8a26970d9b25aa0a36f77c3aee73ece512b7be451496f7a2117b4a7', 'retailer', 'Johnston-Johnson',
        -85.3378135, 119.942060, '{"eco_only": false, "vegan": false}', 2
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'William Weaver', 'lopezanne@example.com', '5b9f1edbf63bb02a308da0c72d503a45811bd545c5d0d2e5aa6563457268ea88', 'consumer', 'Brooks LLC',
        61.833416, 161.416761, '{"eco_only": true, "vegan": false}', 1
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'John Russell', 'jensenrichard@example.org', '7a9cbb2a3ffc61ee5db2ff64d4eb4a1b3cbb125f71ac65526d2671accfb3535e', 'retailer', 'Bright LLC',
        7.7759515, -140.185090, '{"eco_only": true, "vegan": false}', 2
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Devin Riley', 'timothyrobinson@example.net', '4cd21e5eda79cf0640069280df2dcbe0715e08d212c39b4ec9c5c864a7974117', 'restaurant', 'Gardner, Terrell and Martinez',
        -67.785613, -70.341900, '{"eco_only": false, "vegan": true}', 1
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Courtney Simmons', 'pturner@example.net', '0905e1fa3946b57961056e5ee250800885d1f4398f1f1e8f67398bea62319415', 'retailer', 'Blanchard-Saunders',
        20.1102875, -75.485624, '{"eco_only": false, "vegan": true}', 1
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Tom Koch', 'benjaminchambers@example.com', 'caa2464f799bddc90359622bba65367e2981de90ffb6c0319370230ad6c3bd4e', 'farmer', 'Brown, King and Robinson',
        9.608936, 90.911519, '{"eco_only": false, "vegan": true}', 3
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Courtney Gillespie', 'scottjames@example.org', 'c8fa47d0f97444133ba12ef16300f77f93636736aa912b01e616a5a5fb9ab2b7', 'farmer', 'Smith-Medina',
        4.4753155, 56.026806, '{"eco_only": true, "vegan": false}', 3
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Sarah Curry', 'dustin22@example.net', 'd905b403fe17d3a3f9740b36d0d3aeca4ed2289a7c9da682bb31b0ce237b97c6', 'restaurant', 'Kelly, Ortega and Leon',
        36.380936, -1.173907, '{"eco_only": true, "vegan": true}', 2
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Dr. Timothy Alexander', 'davilavanessa@example.com', 'df4f507444327f4bd4f929e4f962fe4a7f462d6bdb41329a533333c46d0b1376', 'farmer', 'Guzman Group',
        25.9088285, -46.833386, '{"eco_only": false, "vegan": false}', 2
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Roy Little', 'bradfordheather@example.org', '1e130993bfadeaebc62d800a5a1544dfd49ee33dee0bcaa7137f194ada2daf47', 'retailer', 'Acosta, Williams and Smith',
        -54.865103, 163.462433, '{"eco_only": true, "vegan": true}', 3
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Brenda Jones', 'lucasmiller@example.net', 'd154f137cc06cfc27a83a92a59690ea4af195ecbd41d2389150165bc733ec443', 'farmer', 'Perez, Simpson and Bowman',
        -67.132384, -169.822231, '{"eco_only": false, "vegan": false}', 3
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Ronald Lara', 'ccross@example.net', '6a1865824eb84c87f3837a245ea4da09486c6109c6e09e2d16ab07727f260a90', 'farmer', 'Singh Ltd',
        -14.581135, 92.576383, '{"eco_only": false, "vegan": false}', 1
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Karen Carrillo', 'jacobellis@example.com', 'efc40ec7035f9c5632cffc95c8c7dc86a4b49afbca8760e5a79e9ca132722e11', 'farmer', 'Montgomery, Lindsey and Ramsey',
        29.3755735, -172.473098, '{"eco_only": false, "vegan": true}', 3
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Elizabeth Allen', 'deborah54@example.org', '72307cd3c34d0552769b5cfd55ee0df2fc581c24e909ffeb548e8d90c85e3710', 'restaurant', 'Stokes Inc',
        4.262079, -11.900451, '{"eco_only": false, "vegan": false}', 2
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Lucas Adams', 'olsongerald@example.org', '2389feeca160650d21f621ac812358c37e17c31072348495826da7e16baf7c07', 'farmer', 'Long, Jones and Bryan',
        25.198939, 160.047721, '{"eco_only": true, "vegan": false}', 3
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Mark Luna', 'fletchersteven@example.net', 'c8842c59a59bc8894578bccc67336301ce97d20d535894317c4fee98d97cbe09', 'retailer', 'Howard, Gamble and Sanchez',
        32.253432, -17.186335, '{"eco_only": false, "vegan": false}', 3
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Cory Jordan', 'michael27@example.org', '72ddc01cc6d2090fb08104f25929f7df8558667f3db3aa3313926f740fe88500', 'restaurant', 'Martin, Benton and Morales',
        39.6353945, 139.787050, '{"eco_only": false, "vegan": true}', 3
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Heather Cantu', 'uthomas@example.org', 'd18d831c8a73bc506fd83527a747b64ac6d336644ea215fe05d5c619e7639762', 'consumer', 'Wright, Elliott and Murray',
        -25.9006685, -32.408122, '{"eco_only": false, "vegan": true}', 1
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Jeremy Miller', 'ugarcia@example.com', '2cc1bdf8477422b882282966e68c6d3f0703c6e1df04c06d51259dd1e4c5e427', 'consumer', 'Kennedy, Mills and Cruz',
        -58.967579, -133.121234, '{"eco_only": true, "vegan": false}', 1
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Jeremy Kim', 'jcarter@example.net', '9c3fa381e435d8d0656d95f3c3cdf1b92ec74db2c5be7142394d2d2a13a4d2e5', 'farmer', 'Mejia Inc',
        -20.372435, -36.520454, '{"eco_only": true, "vegan": true}', 2
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Christian Watson', 'stephaniebennett@example.com', 'd9b61092e2b304b870529ace246fe2be24e568ff8a40f7138375f2aec9189687', 'consumer', 'Gomez PLC',
        -81.306385, 177.055135, '{"eco_only": false, "vegan": false}', 3
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Jesse Gonzalez', 'qwilson@example.com', 'e651c89e905853e66a493e965913f58358b6369485308b63a9f788690aa08764', 'farmer', 'Jefferson, Jenkins and Anderson',
        -35.832468, 107.202215, '{"eco_only": false, "vegan": false}', 3
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Spencer Young', 'hansenwilliam@example.net', '332b1c51a30abb049d4f3d50eef5e7fb385d88bce8f2fa028e3bfa9ecd8a4e37', 'retailer', 'Harrison-Tucker',
        62.9817815, -124.209330, '{"eco_only": true, "vegan": true}', 3
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Leslie Humphrey', 'christine04@example.org', 'f531d1c48bbfc33fe3de5432677e4fbbc16725128474df14c440b5bf1c661b8f', 'consumer', 'Rosales-Ruiz',
        46.901316, 176.192800, '{"eco_only": true, "vegan": false}', 1
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Melissa Marks', 'emccullough@example.com', 'fe33990a95bc7c73c4d95e1875851d9d3575cba7747809045cc7939348e3f0df', 'retailer', 'Daniel PLC',
        16.228302, -53.556248, '{"eco_only": true, "vegan": true}', 3
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Christina Sheppard', 'xhumphrey@example.net', 'dbee9df5af846168f359fbcec1b85355f8079b9ded3d6129cfe5abb0e774e68e', 'restaurant', 'Donaldson LLC',
        -7.8207205, -140.349743, '{"eco_only": true, "vegan": true}', 1
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Danielle Clayton', 'cruzbrian@example.org', 'f639a5891664cced0aa04feb02529bcaac579d7928fe3a782445f822c2ff8eef', 'farmer', 'Hess Ltd',
        10.284285, 55.604340, '{"eco_only": true, "vegan": false}', 3
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Nicole Arnold', 'sarah19@example.org', '518b1ecee86f0644cff130b355821a71ad63310e47f22108e8b3a6883885e518', 'consumer', 'Jones Ltd',
        83.1560155, 64.827900, '{"eco_only": false, "vegan": false}', 3
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Taylor Lee', 'garciabrittany@example.org', 'b24cc120261949c818af25bc1ae449751edad71a626d771b131bead31570f440', 'retailer', 'Thompson-Knight',
        -36.7366215, -109.673591, '{"eco_only": false, "vegan": false}', 2
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Phillip Gould', 'chaneytara@example.com', 'b7d197a10a692fe99179f7afd10d28d6d01720bdf7ba52802aef5fb2ada9c25e', 'farmer', 'Robinson, Henderson and Hill',
        -6.810731, 125.812150, '{"eco_only": false, "vegan": false}', 1
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Gabriella Riddle', 'wcarroll@example.com', '29ed49b6caf68ddcba3477b2013a2f45b024d5a3629ec7310e3ab4a627dda2cb', 'farmer', 'Walters LLC',
        -3.328821, 90.909331, '{"eco_only": false, "vegan": false}', 1
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Brandon Walters', 'jonathan48@example.org', 'c0783ded9487d4e702eb5e63e26bc418557b140130fe73155dca280601937558', 'retailer', 'Walker Inc',
        56.2283365, -159.597917, '{"eco_only": false, "vegan": false}', 2
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Christopher Brooks', 'shawnmullins@example.org', 'ed1df249b5446d4fcccbc47341dccb0a6adf2b2ae1833f2757396d7be1e047a2', 'retailer', 'Jordan Group',
        48.302426, -36.471730, '{"eco_only": true, "vegan": false}', 1
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Jerry Herrera', 'lynnmitchell@example.net', '2bbcd0630bf63d82296a6316ebc4a7a40d81004b2f9f6ecaec63bf9cc3590c1d', 'restaurant', 'Yang, Castro and Gordon',
        -40.4347895, -74.846086, '{"eco_only": true, "vegan": true}', 3
    ); 
INSERT INTO users (
        name, email, password_hash, role, entity_name, location_lat, location_lon, preferences, intake_profile_id
    ) VALUES (
        'Evan Freeman', 'macdonaldadam@example.net', '0e0e41bce35e6ff224fff4b4fa87af66d6bd63d9e9edf17ea390535338e2290b', 'restaurant', 'Gonzalez, Parker and Waller',
        53.6049025, 135.762963, '{"eco_only": false, "vegan": true}', 2
    );


--- PRODUCTS
INSERT INTO products (
    name, description, price, currency, unit, category, nutritional_info,
    stock_available, expiration_date, is_eco, image_url, provider_id
) VALUES
('Zanahoria', 'Producto natural y saludable', 8.42, 'EUR', 'kg', 'vegetables', '{"kcal": 41, "protein": 0.9}', 76.31, '2025-06-07', FALSE, 'https://example.com/image.jpg', 4),
('Brócoli', 'Producto natural y saludable', 6.42, 'EUR', 'kg', 'vegetables', '{"kcal": 55, "protein": 3.7}', 71.42, '2025-06-08', TRUE, 'https://example.com/image.jpg', 1),
('Quinoa', 'Producto natural y saludable', 9.66, 'EUR', 'kg', 'grains', '{"kcal": 120, "protein": 4.1}', 99.36, '2025-06-24', TRUE, 'https://example.com/image.jpg', 6),
('Tofu', 'Producto natural y saludable', 1.74, 'EUR', 'kg', 'legumes', '{"kcal": 76, "protein": 8}', 72.49, '2025-06-08', FALSE, 'https://example.com/image.jpg', 5),
('Plátano', 'Producto natural y saludable', 6.45, 'EUR', 'kg', 'fruits', '{"kcal": 89, "protein": 1.1}', 35.15, '2025-06-03', TRUE, 'https://example.com/image.jpg', 8),
('Manzana', 'Producto natural y saludable', 4.99, 'EUR', 'kg', 'fruits', '{"kcal": 52, "protein": 0.3}', 50.00, '2025-06-10', TRUE, 'https://example.com/image.jpg', 2),
('Almendra', 'Producto natural y saludable', 12.50, 'EUR', 'kg', 'nuts', '{"kcal": 575, "protein": 21.2}', 20.00, '2025-07-01', TRUE, 'https://example.com/image.jpg', 3),
('Salmón fresco', 'Producto fresco y saludable', 25.00, 'EUR', 'kg', 'fish', '{"kcal": 206, "protein": 22}', 15.00, '2025-06-15', FALSE, 'https://example.com/image.jpg', 7),
('Pollo orgánico', 'Producto fresco y saludable', 15.00, 'EUR', 'kg', 'meat', '{"kcal": 239, "protein": 27}', 30.00, '2025-06-20', TRUE, 'https://example.com/image.jpg', 9),
('Arroz integral', 'Producto natural y saludable', 3.50, 'EUR', 'kg', 'grains', '{"kcal": 111, "protein": 2.6}', 100.00, '2025-06-30', TRUE, 'https://example.com/image.jpg', 10),
('Lentejas rojas', 'Producto natural y saludable', 2.00, 'EUR', 'kg', 'legumes', '{"kcal": 116, "protein": 9}', 80.00, '2025-07-05', FALSE, 'https://example.com/image.jpg', 11),
('Espinaca orgánica', 'Producto fresco y saludable', 4.00, 'EUR', 'kg', 'vegetables', '{"kcal": 23, "protein": 2.9}', 60.00, '2025-06-25', TRUE, 'https://example.com/image.jpg', 12),
('Aguacate', 'Producto natural y saludable', 5.00, 'EUR', 'kg', 'fruits', '{"kcal": 160, "protein": 2}', 40.00, '2025-06-12', TRUE, 'https://example.com/image.jpg', 13),
('Chía orgánica', 'Producto natural y saludable', 10.00, 'EUR', 'kg', 'seeds', '{"kcal": 486, "protein": 16.5}', 25.00, '2025-07-10', TRUE, 'https://example.com/image.jpg', 14),
('Pescado azul', 'Producto fresco y saludable', 20.00, 'EUR', 'kg', 'fish', '{"kcal": 205, "protein": 20}', 18.00, '2025-06-18', FALSE, 'https://example.com/image.jpg', 15),
('Carne de res orgánica', 'Producto fresco y saludable', 30.00, 'EUR', 'meat', '{"kcal": 250, "protein": 26}', 22.00, '2025-06-22', TRUE, 'https://example.com/image.jpg', 16),
('Batata', 'Producto natural y saludable', 3.00, 'EUR', 'kg', 'vegetables', '{"kcal": 86, "protein": 1.6}', 55.00, '2025-06-28', TRUE, 'https://example.com/image.jpg', 17),
('Cacahuate orgánico', 'Producto natural y saludable', 8.00, 'EUR', 'kg', 'nuts', '{"kcal": 567, "protein": 25.8}', 30.00, '2025-07-15', TRUE, 'https://example.com/image.jpg', 18),
('Pasta integral', 'Producto natural y saludable', 2.50, 'EUR', 'grains', '{"kcal": 124, "protein": 4.3}', 90.00, '2025-07-20', TRUE, 'https://example.com/image.jpg', 19),
('Garbanzos orgánicos', 'Producto natural y saludable', 1.80, 'EUR', 'legumes', '{"kcal": 164, "protein": 8.9}', 70.00, '2025-07-25', FALSE, 'https://example.com/image.jpg', 20);


INSERT INTO recipes (
        name, description, author_id, image_url, steps, time_minutes, difficulty,
        is_vegan, is_gluten_free, tags, nutrition_total
    ) VALUES (
        'Still Delight', 'Finally history both blood certain article option.', 3,
        'https://placeimg.com/982/621/any', '["Ever decide deal ever course.", "Thing where operation success measure human people clear.", "Relationship expert detail subject process market about."]', 28, 'medium',
        FALSE, FALSE,
        '["saludable", "r\u00e1pida"]', '{"kcal": 641, "protein": 10.28514637805656}'
    );

INSERT INTO recipes (
        name, description, author_id, image_url, steps, time_minutes, difficulty,
        is_vegan, is_gluten_free, tags, nutrition_total
    ) VALUES (
        'Market Delight', 'Debate interesting another truth.', 4,
        'https://placekitten.com/571/54', '["Course focus try middle cause.", "One kid final must few make.", "Hope much store."]', 12, 'medium',
        FALSE, TRUE,
        '["saludable", "r\u00e1pida"]', '{"kcal": 407, "protein": 11.41624565069966}'
    );

INSERT INTO recipes (
        name, description, author_id, image_url, steps, time_minutes, difficulty,
        is_vegan, is_gluten_free, tags, nutrition_total
    ) VALUES (
        'Consider Delight', 'Himself impact study visit who growth debate.', 3,
        'https://placekitten.com/162/932', '["Finish wish glass trouble knowledge parent cell.", "Increase rather eye.", "Everyone explain share production represent work look agreement."]', 19, 'medium',
        FALSE, FALSE,
        '["saludable", "r\u00e1pida"]', '{"kcal": 682, "protein": 19.165137382555493}'
    );

INSERT INTO recipes (
        name, description, author_id, image_url, steps, time_minutes, difficulty,
        is_vegan, is_gluten_free, tags, nutrition_total
    ) VALUES (
        'Green Delight', 'Court serious audience small effort police land.', 2,
        'https://placekitten.com/604/488', '["Feeling inside computer rate attention strong.", "Call edge maybe carry.", "Skin appear very half part strategy real."]', 30, 'medium',
        FALSE, TRUE,
        '["saludable", "r\u00e1pida"]', '{"kcal": 630, "protein": 19.31869563895775}'
    );

INSERT INTO recipes (
        name, description, author_id, image_url, steps, time_minutes, difficulty,
        is_vegan, is_gluten_free, tags, nutrition_total
    ) VALUES (
        'Teach Delight', 'Party budget necessary traditional forward.', 3,
        'https://placekitten.com/856/976', '["Require fill stop on former light too enjoy.", "Pass network social some.", "Actually everything room such choice."]', 43, 'medium',
        TRUE, TRUE,
        '["saludable", "r\u00e1pida"]', '{"kcal": 275, "protein": 29.170727371752946}'
    );

INSERT INTO recipe_ingredients (
        recipe_id, product_id, name, quantity, unit, nutritional_info, optional
    ) VALUES (
        3, 3, 'fill', 0.42, 'kg',
        '{"kcal": 40, "protein": 1.6836259853823354}', FALSE
    );

INSERT INTO recipe_ingredients (
        recipe_id, product_id, name, quantity, unit, nutritional_info, optional
    ) VALUES (
        2, 9, 'pay', 0.71, 'kg',
        '{"kcal": 31, "protein": 1.9281770121283923}', FALSE
    );

INSERT INTO recipe_ingredients (
        recipe_id, product_id, name, quantity, unit, nutritional_info, optional
    ) VALUES (
        1, 8, 'to', 0.84, 'kg',
        '{"kcal": 45, "protein": 2.4245070162321603}', FALSE
    );

INSERT INTO recipe_ingredients (
        recipe_id, product_id, name, quantity, unit, nutritional_info, optional
    ) VALUES (
        2, 4, 'amount', 0.88, 'kg',
        '{"kcal": 51, "protein": 4.9904050026503395}', FALSE
    );

INSERT INTO recipe_ingredients (
        recipe_id, product_id, name, quantity, unit, nutritional_info, optional
    ) VALUES (
        5, 1, 'activity', 0.94, 'kg',
        '{"kcal": 83, "protein": 1.5013997730433415}', TRUE
    );

INSERT INTO recipe_ingredients (
        recipe_id, product_id, name, quantity, unit, nutritional_info, optional
    ) VALUES (
        4, 4, 'before', 1.00, 'kg',
        '{"kcal": 72, "protein": 1.4534809578089698}', FALSE
    );

INSERT INTO recipe_ingredients (
        recipe_id, product_id, name, quantity, unit, nutritional_info, optional
    ) VALUES (
        1, 7, 'policy', 0.96, 'kg',
        '{"kcal": 98, "protein": 2.669173565914808}', TRUE
    );

INSERT INTO recipe_ingredients (
        recipe_id, product_id, name, quantity, unit, nutritional_info, optional
    ) VALUES (
        5, 1, 'huge', 0.37, 'kg',
        '{"kcal": 97, "protein": 4.068944001726367}', TRUE
    );

INSERT INTO recipe_ingredients (
        recipe_id, product_id, name, quantity, unit, nutritional_info, optional
    ) VALUES (
        4, 8, 'huge', 0.39, 'kg',
        '{"kcal": 47, "protein": 3.2206462751016867}', TRUE
    );

INSERT INTO recipe_ingredients (
        recipe_id, product_id, name, quantity, unit, nutritional_info, optional
    ) VALUES (
        4, 5, 'whether', 0.69, 'kg',
        '{"kcal": 89, "protein": 2.123546114232827}', TRUE
    );

INSERT INTO shopping_lists (
        user_id, created_at, total_price, currency, status
    ) VALUES (
        3, CURRENT_TIMESTAMP, 17.28, 'EUR', 'active'
    );

INSERT INTO shopping_lists (
        user_id, created_at, total_price, currency, status
    ) VALUES (
        2, CURRENT_TIMESTAMP, 20.61, 'EUR', 'active'
    );

INSERT INTO shopping_lists (
        user_id, created_at, total_price, currency, status
    ) VALUES (
        5, CURRENT_TIMESTAMP, 49.92, 'EUR', 'active'
    );

INSERT INTO sensor_readings (
        device_id, product_id, temperature, humidity, gas_level, voltage, timestamp
    ) VALUES (
        'device_0', 1, 4.4,
        40.6, 0.69, 3.46, CURRENT_TIMESTAMP
    );

INSERT INTO sensor_readings (
        device_id, product_id, temperature, humidity, gas_level, voltage, timestamp
    ) VALUES (
        'device_1', 2, 4.6,
        48.9, 0.94, 3.30, CURRENT_TIMESTAMP
    );

INSERT INTO sensor_readings (
        device_id, product_id, temperature, humidity, gas_level, voltage, timestamp
    ) VALUES (
        'device_2', 2, 7.6,
        79.3, 0.12, 3.44, CURRENT_TIMESTAMP
    );

INSERT INTO sensor_readings (
        device_id, product_id, temperature, humidity, gas_level, voltage, timestamp
    ) VALUES (
        'device_3', 2, 4.4,
        68.7, 0.79, 3.07, CURRENT_TIMESTAMP
    );

INSERT INTO sensor_readings (
        device_id, product_id, temperature, humidity, gas_level, voltage, timestamp
    ) VALUES (
        'device_4', 3, 7.7,
        60.6, 0.98, 3.12, CURRENT_TIMESTAMP
    );

INSERT INTO impact_metrics (
        user_id, co2_saved_kg, local_support_eur, waste_prevented_kg
    ) VALUES (
        3, 3.76, 6.66, 1.02
    );

INSERT INTO impact_metrics (
        user_id, co2_saved_kg, local_support_eur, waste_prevented_kg
    ) VALUES (
        1, 1.99, 6.01, 1.64
    );

INSERT INTO impact_metrics (
        user_id, co2_saved_kg, local_support_eur, waste_prevented_kg
    ) VALUES (
        2, 4.90, 8.26, 0.73
    );

INSERT INTO impact_metrics (
        user_id, co2_saved_kg, local_support_eur, waste_prevented_kg
    ) VALUES (
        3, 4.63, 5.67, 1.49
    );

INSERT INTO impact_metrics (
        user_id, co2_saved_kg, local_support_eur, waste_prevented_kg
    ) VALUES (
        4, 4.77, 8.85, 1.02
    );

INSERT INTO logistics_routes (
        driver_name, distance_km, orders_ids, created_at, vehicle_type, estimated_time_min
    ) VALUES (
        'Sonya Kelly', 49.05, '[1, 6]',
        CURRENT_TIMESTAMP, 'bike', 56
    );

INSERT INTO logistics_routes (
        driver_name, distance_km, orders_ids, created_at, vehicle_type, estimated_time_min
    ) VALUES (
        'Thomas Cruz', 29.03, '[7, 9]',
        CURRENT_TIMESTAMP, 'van', 49
    );

INSERT INTO logistics_routes (
        driver_name, distance_km, orders_ids, created_at, vehicle_type, estimated_time_min
    ) VALUES (
        'Makayla Chan', 19.33, '[5, 5]',
        CURRENT_TIMESTAMP, 'car', 72
    );
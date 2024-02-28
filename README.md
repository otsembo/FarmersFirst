# FarmersFirst App

FarmersFirst is a mobile application designed to streamline the shopping experience for farmers. It allows farmers to manage their baskets, view product recommendations, and make purchases conveniently. This README provides instructions for setting up and running the application.


## Features

- **Basket Management**: Farmers can add, remove, and update items in their baskets.
- **Product Recommendations**: The application provides personalized product recommendations based on previous purchases.
- **Google OAuth 2.0**: Farmers can sign in securely using their Google accounts.

[![Watch the video](https://img.youtube.com/vi/Zcivg9hyaYo/hqdefault.jpg)](https://www.youtube.com/embed/Zcivg9hyaYo)

## Setup

Follow these steps to set up and run the FarmersFirst application:

1. **Clone the Repository**: Clone the FarmersFirst repository to your local machine using Git:

    ```bash
    git clone https://github.com/otsembo/FarmersFirst.git
    ```

2. **Setup Recommender (To Be Completed)**: [Instructions for setting up the recommender will be provided here.]

3. **Setup Google OAuth (To Be Completed)**: [Instructions for setting up Google OAuth will be provided here.]

4. **Open Project in Android Studio**: Open the FarmersFirst project in Android Studio.

5. **Build and Run**: Build the project and run it on an Android device or emulator.

## Usage

Once the application is set up and running, follow these steps to use it:

1. **Sign In**: Sign in to the application using your Google account. If you are using an emulator to test, ensure you use one that has Google Play Services installed.

2. **Browse Products**: Browse through the available products and add them to your basket.

3. **Manage Basket**: Manage your basket by adding, removing, or updating items.

4. **View Recommendations**: View personalized product recommendations based on your purchase history.

5. **Checkout**: Proceed to checkout to complete your purchase.

## Tools

FarmersFirst is built with the following tools and technologies:

- Kotlin Coroutines
- Koin for Dependency Injection
- Room Persistence Library
- WorkManager
- Retrofit for network requests

## Contributing

Contributions to the FarmersFirst project are welcome! If you find any bugs or have suggestions for improvements, please open an issue or create a pull request on GitHub.

## License

FarmersFirst is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Acknowledgements

FarmersFirst uses the following open-source libraries:

- Kotlin Coroutines
- Koin for Dependency Injection
- Room Persistence Library
- WorkManager
- Retrofit for network requests

## Authors

- [Ian Okumu](https://github.com/otsembo)

## Contact

For inquiries or support, please contact [okumu.otsembo@gmail.com](mailto:okumu.otsembo@gmail.com).

## Additional Setup Instructions

### Gradle Secrets

Before building the project, make sure to set up the following Google Gradle secrets:

- `googleOAuthKey`: [Your Google OAuth Client ID]
- `GeminiApiKey`: [Your Gemini Api Key]

These secrets should be added to your `local.properties` file in the project root directory.

```gradle
googleOAuthKey="Client ID here"
GeminiApiKey="Key Here"
```

### Recommender Setup

[Instructions for setting up the recommender will be provided here.]

### Google OAuth Setup

[Instructions for setting up Google OAuth will be provided here.]


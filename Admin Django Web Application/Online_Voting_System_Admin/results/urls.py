from django.urls import path
from .views import *
urlpatterns =[

        path("results",results,name="results"),
        path("resultsofpolls",resultsofpolls,name="resultsofpolls"),

]
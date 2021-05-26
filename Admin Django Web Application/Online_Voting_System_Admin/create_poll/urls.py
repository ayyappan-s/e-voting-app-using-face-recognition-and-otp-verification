from django.urls import path
from .views import *
urlpatterns=[
    path("create_poll",create_poll,name='create_poll'),
    path("add_candidate",add_candidate,name='add_candidate'),
]
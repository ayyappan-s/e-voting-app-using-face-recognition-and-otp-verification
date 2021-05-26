"""Online_Voting_System_Admin URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/3.2/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.shortcuts import redirect
from django.urls import include,path
# from user_login.views import login_ajax_request
urlpatterns = [
    path('admin/', admin.site.urls),
    # path('accounts/',include('django.contrib.auth.urls')),
    path("",include('user_login.urls')),
    path("",include('add_voters.urls')),
    path("",include('create_poll.urls')),
    path("",include('dashboard.urls')),
    path("",include('results.urls')),
    path("",include('user_voting.urls')),
]

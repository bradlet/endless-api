terraform {
  required_providers {
    aws = {
      source = "hashicorp/aws"
      version = "~> 4.0"
    }
  }
}

provider "aws" {
  region = var.region
}

module "ecs" {
  source = "terraform-aws-modules/ecs/aws"

  cluster_name = "ecs-fargate"

  fargate_capacity_providers = {
    FARGATE = {
      default_capacity_provider_strategy = {
        weight = 50
      }
    }
    FARGATE_SPOT = {
      default_capacity_provider_strategy = {
        weight = 50
      }
    }
  }
}

data "aws_ecr_image" "endless-api" {
  repository_name = var.ecr_repo
  image_tag = "latest"
}

resource "aws_ecs_task_definition" "api" {
  family = "service"
  requires_compatibilities = ["FARGATE"]
  network_mode = "awsvpc"
  cpu = 256
  memory = 512
  container_definitions = jsonencode([
    {
      name = var.svc_display_name
      image = data.aws_ecr_image.endless-api.id
      cpu = 256
      memory = 512
      essential = true
      portMappings = [
        {
          containerPort = 8080
          hostPort = 8080
        }
      ]
    }
  ])
}

resource "aws_vpc" "default" {
  cidr_block = var.vpc_cidr
  instance_tenancy = "default"
}

resource "aws_subnet" "default_subnet" {
  vpc_id = aws_vpc.default.id
  cidr_block = var.vpc_cidr
}

resource "aws_ecs_service" "service" {
  name = var.svc_display_name
  cluster = module.ecs.cluster_arn
  task_definition = aws_ecs_task_definition.api.arn

  force_new_deployment = true
  launch_type = "FARGATE"

  network_configuration {
    subnets = [aws_subnet.default_subnet.id]
  }
}

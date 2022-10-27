terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
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

  tags = {
    "group" : var.group_name
  }
}

module "vpc" {
  source = "terraform-aws-modules/vpc/aws"

  name = var.vpc_name

  cidr            = var.vpc_cidr
  azs             = ["${var.region}a", "${var.region}b", "${var.region}c"]
  private_subnets = ["20.0.1.0/24"]
  public_subnets  = ["20.0.2.0/24", "20.0.3.0/24"]

  enable_nat_gateway = false
  single_nat_gateway = true

  tags = {
    "group" : var.group_name
  }
}

module "sg" {
  source = "terraform-aws-modules/security-group/aws//modules/http-80"

  name        = "endless-server-sg"
  description = "Security group opening HTTP ingress and egress"
  vpc_id      = module.vpc.vpc_id

  ingress_cidr_blocks = module.vpc.public_subnets_cidr_blocks

  tags = {
    "group" : var.group_name
  }
}

module "alb" {
  source  = "terraform-aws-modules/alb/aws"
  version = "~> 6.0"

  name = var.alb_name

  load_balancer_type = "application"

  vpc_id          = module.vpc.vpc_id
  subnets         = module.vpc.public_subnets
  security_groups = [module.sg.security_group_id]

  target_groups = [
    {
      backend_protocol = "HTTP"
      backend_port     = var.svc_port
      target_type      = "ip"
    }
  ]

  http_tcp_listeners = [
    {
      port               = 80
      protocol           = "HTTP"
      target_group_index = 0
    }
  ]

  tags = {
    "group" : var.group_name
  }
}



data "aws_ecr_image" "endless-api" {
  repository_name = var.ecr_repo
  image_tag       = "latest"
}

resource "aws_ecs_task_definition" "api" {
  family                   = "service"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = 256
  memory                   = 512
  container_definitions = jsonencode([
    {
      name      = var.svc_display_name
      image     = data.aws_ecr_image.endless-api.id
      cpu       = 256
      memory    = 512
      essential = true
      portMappings = [
        {
          containerPort = var.svc_port
        }
      ]
    }
  ])

  tags = {
    "group" : var.group_name
  }
}

resource "aws_ecs_service" "service" {
  depends_on      = [module.alb.http_tcp_listener_ids]
  name            = var.svc_display_name
  cluster         = module.ecs.cluster_arn
  task_definition = aws_ecs_task_definition.api.arn
  desired_count   = 1

  force_new_deployment = true
  launch_type          = "FARGATE"

  network_configuration {
    subnets          = module.vpc.public_subnets
    security_groups  = [module.sg.security_group_id]
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = module.alb.target_group_arns[0]
    container_name   = var.svc_display_name
    container_port   = var.svc_port
  }

  tags = {
    "group" : var.group_name
  }
}

resource "aws_resourcegroups_group" "endless_group" {
  name = "Endless-API-Infrastructure"
  resource_query {
    query = jsonencode({
      ResourceTypeFilters : ["AWS::AllSupported"],
      TagFilters : [
        {
          Key : "group",
          Values : [var.group_name]
        }
      ]
    })
  }
}

#!/usr/bin/python3

from os import path, makedirs
import argparse
import random
import uuid
from datetime import datetime, timedelta
from typing import NamedTuple

output_path = f"generated/{datetime.now().strftime('%Y-%m-%d_%H-%M')}_data.sql"


class Customer(NamedTuple):
    id: str
    email: str
    first_name: str
    last_name: str
    birthday: str
    created: str
    last_modified: str


class Product(NamedTuple):
    id: str
    name: str
    created: str
    last_modified: str

class Tag(NamedTuple):
    id: str
    name: str
    created: str
    last_modified: str

class Order(NamedTuple):
    id: str
    customer_id: str
    product_id: str
    quantity: int
    state: str
    created: str
    last_modified: str


parser = argparse.ArgumentParser()
parser.add_argument("-c", "--customers", help="Number of customer records to generate", type=int, default=100)
parser.add_argument("-t", "--tags", help="Number of product tags to generate", type=int, default=50)
parser.add_argument("-p", "--products", help="Number of product records to generate", type=int, default=100)
parser.add_argument("-o", "--orders", help="Number of order records to generate", type=int, default=1000)

customer_ids = set()
tag_ids = set()
product_ids = set()


def build_customer_sql(customer: Customer):
    return f"""
            INSERT INTO customers(id, email, first_name, last_name, birthday, created, last_modified) VALUES
                ('{customer.id}', '{customer.email}', '{customer.first_name}', '{customer.last_name}', '{customer.birthday}'
                , '{customer.created}', '{customer.last_modified}'); 
        """.strip()


def build_tag_sql(tag: Tag):
    return f"""
            INSERT INTO tags(id, name, created, last_modified) VALUES 
            ('{tag.id}', '{tag.name}', '{tag.created}', '{tag.last_modified}');
    """.strip()


def build_product_sql(product: Product):
    return f"""
            INSERT INTO products(id, name, created, last_modified) VALUES
                ('{product.id}', '{product.name}', '{product.created}', '{product.last_modified}'); 
        """.strip()


def build_order_sql(order: Order):
    return f"""
            INSERT INTO customer_orders(id, customer_id, product_id, quantity, state, created, last_modified) VALUES
                ('{order.id}', '{order.customer_id}', '{order.product_id}', '{order.quantity}', '{order.state}'
                , '{order.created}', '{order.last_modified}'); 
        """.strip()


def generate_orders(count: int):
    with open(output_path, "a") as output:
        output.write("BEGIN")
        output.write("\n")
        for counter in range(count):
            id = str(uuid.uuid4())
            customer_id = random.choice(list(customer_ids))
            product_id = random.choice(list(product_ids))
            state = random.choice(['NEW', 'ACCEPTED', 'IN_PROGRESS', 'SUCCESS', 'FAILURE'])
            created = datetime.now() - timedelta(days=random.randint(0, 500))
            last_modified = created + timedelta(days=random.randint(0, 500))
            if last_modified > datetime.now():
                last_modified = datetime.now()
            order = Order(
                id,
                customer_id,
                product_id,
                random.randint(1, 100),
                state,
                created.isoformat(),
                last_modified.isoformat(),
            )
            statement = build_order_sql(order)
            output.write(statement)
            output.write("\n")
        output.write("COMMIT;")
        output.write("\n")


def generate_tags(count: int):
    with open(output_path, "a") as output:
        output.write("BEGIN")
        output.write("\n")
        for counter in range(count):
            id = str(uuid.uuid4())
            name = f"Fancy tag #{counter}"
            created = datetime.now() - timedelta(days=random.randint(0, 500))
            last_modified = created + timedelta(days=random.randint(0, 500))
            if last_modified > datetime.now():
                last_modified = datetime.now()
            tag_ids.add(id)
            tag = Tag(
                id,
                name,
                created.isoformat(),
                last_modified.isoformat(),
            )
            statement = build_tag_sql(tag)
            output.write(statement)
            output.write("\n")
        output.write("COMMIT;")
        output.write("\n")


def generate_products(count: int):
    with open(output_path, "a") as output:
        output.write("BEGIN")
        output.write("\n")
        for counter in range(count):
            id = str(uuid.uuid4())
            created = datetime.now() - timedelta(days=random.randint(0, 500))
            last_modified = created + timedelta(days=random.randint(0, 500))
            if last_modified > datetime.now():
                last_modified = datetime.now()
            product_ids.add(id)
            product = Product(
                id,
                f"Fancy product #{counter}",
                created.isoformat(),
                last_modified.isoformat(),
            )
            statement = build_product_sql(product)
            output.write(statement)
            output.write("\n")
        output.write("COMMIT;")
        output.write("\n")


def generate_customers(count: int):
    with open(output_path, "a") as output:
        output.write("BEGIN")
        output.write("\n")
        for counter in range(count):
            id = str(uuid.uuid4())
            created = datetime.now() - timedelta(days=random.randint(0, 500))
            last_modified = created + timedelta(days=random.randint(0, 500))
            if last_modified > datetime.now():
                last_modified = datetime.now()
            customer_ids.add(id)
            customer = Customer(
                id,
                f"max.mustermann_{counter}@gmail.com",
                f"Max {counter}",
                f"Mustermann {counter}",
                (datetime.now() - timedelta(days=random.randint(10_000, 25_000))).strftime("%Y-%m-%d"),
                created.isoformat(),
                last_modified.isoformat(),
            )
            statement = build_customer_sql(customer)
            output.write(statement)
            output.write("\n")
        output.write("COMMIT;")
        output.write("\n")


def main():
    args = parser.parse_args()
    print(f"Arguments {args=}")
    if not path.exists(path.dirname(output_path)):
        makedirs(path.dirname(output_path))
    generate_customers(args.customers)
    generate_tags(args.tags)
    generate_products(args.products)
    generate_orders(args.orders)


if __name__ == '__main__':
    main()
